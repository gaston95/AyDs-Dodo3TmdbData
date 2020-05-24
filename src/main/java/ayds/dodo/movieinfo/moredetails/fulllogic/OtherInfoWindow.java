package ayds.dodo.movieinfo.moredetails.fulllogic;

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class OtherInfoWindow {
  private JPanel contentPane;
  private JTextPane descriptionTextPane;
  private JPanel imagePanel;

  private static final String movie_in_db = "[*]";

  public static void open(OmdbMovie movie) {
    OtherInfoWindow win = createWindow();

    win.getMoviePlot(movie);
  }

  private static OtherInfoWindow createWindow() {
    OtherInfoWindow win = new OtherInfoWindow();

    win.contentPane = new JPanel();
    win.contentPane.setLayout(new BoxLayout(win.contentPane, BoxLayout.PAGE_AXIS));
    win.contentPane.add(new JLabel("Data from The Movie Data Base"));

    win.imagePanel = new JPanel();
    win.contentPane.add(win.imagePanel);

    JPanel descriptionPanel = new JPanel(new BorderLayout());
    win.descriptionTextPane = new JTextPane();
    win.descriptionTextPane.setEditable(false);
    win.descriptionTextPane.setContentType("text/html");
    win.descriptionTextPane.setMaximumSize(new Dimension(600, 400));
    descriptionPanel.add(win.descriptionTextPane);
    win.contentPane.add(descriptionPanel);


    JFrame frame = new JFrame("Movie Info Dodo");
    frame.setMinimumSize(new Dimension(600, 600));
    frame.setContentPane(win.contentPane);
    frame.pack();
    frame.setVisible(true);

    return win;
  }

  public void getMoviePlot(OmdbMovie movie) {

    descriptionTextPane.setContentType("text/html");

    setHyperLinkListener();

    new Thread(() -> {

      DataBase.createNewDatabase();
      String text = DataBase.getOverview(movie.getTitle());
      String path = DataBase.getImageUrl(movie.getTitle());

      if (movieExistsInDb(text,path)) {
        text = getTextInDB(text);
      } else {
        try {
          text = "No Results";
          path = "https://www.themoviedb.org/assets/2/v4/logos/256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png";

          JsonObject searchResult = searchMovie(movie);

          if(searchResult != null){

            JsonElement extract = searchResult.get("overview");
            if (isNotNull(extract)) {
              text = extract.getAsString().replace("\\n", "\n");
              text = textToHtml(text, movie.getTitle());

              JsonElement backdropPathJson = searchResult.get("backdrop_path");
              if (isNotNull(backdropPathJson)) {
                path = "https://image.tmdb.org/t/p/w400/" + backdropPathJson.getAsString();
              }

              JsonElement posterPath = searchResult.get("poster_path");
              if(isNotNull(posterPath))
                text += "\n" + "<a href=https://image.tmdb.org/t/p/w400/" + posterPath.getAsString() +">View Movie Poster</a>";

              DataBase.saveMovieInfo(movie.getTitle(), text, path);
            }
          }
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }
      descriptionTextPane.setText(text);

      setImage(path);
    }).start();
  }

  private boolean isNotNull(JsonElement element) {
    return element != null && !element.isJsonNull();
  }
  private static String getTextInDB(String text) {
    return movie_in_db + text;
  }

  private TheMovieDBAPI createAPI(){
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

    return retrofit.create(TheMovieDBAPI.class);
  }

  private void setHyperLinkListener(){
    descriptionTextPane.addHyperlinkListener(e -> {
      if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
        Desktop desktop = Desktop.getDesktop();
        try {
          desktop.browse(e.getURL().toURI());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
  }

  private boolean movieExistsInDb(String text, String path) {
    return text != null && path != null;
  }

  private JsonObject searchMovie(OmdbMovie movie) {
    TheMovieDBAPI tmdbAPI = createAPI();
    Response<String> callResponse;
    try {
      callResponse = tmdbAPI.getTerm(movie.getTitle()).execute();

      Gson gson = new Gson();
      JsonObject jobj = gson.fromJson(callResponse.body(), JsonObject.class);

      Iterator<JsonElement> resultIterator = jobj.get("results").getAsJsonArray().iterator();
      JsonObject result;

      while (resultIterator.hasNext()) {
        result = resultIterator.next().getAsJsonObject();

        if (areSameYear(result,movie.getYear())) {
          return result;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private boolean areSameYear(JsonObject result, String movieYear) {
    JsonElement yearJson = result.get("release_date");
    String year = yearJson == null ? "" : yearJson.getAsString().split("-")[0];

    return year.equals(movieYear);
  }

  private void setImage(String path) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
    }
    try {
      URL url = new URL(path);
      BufferedImage image = ImageIO.read(url);
      JLabel label = new JLabel(new ImageIcon(image));
      imagePanel.add(label);

      contentPane.validate();
      contentPane.repaint();

    } catch (Exception exp) {
      exp.printStackTrace();
    }
  }

  private static final String html_open = "<html>";
  private static final String body_open = "<body style=\"width: 400px\">";
  private static final String font_open = "<font face=\"arial\">";
  private static final String font_close = "</font>";

  public static String textToHtml(String text, String term) {

    StringBuilder builder = new StringBuilder();
    builder.append(html_open + body_open)
            .append(font_open);

    String textWithReplacedQuotes = replaceQuotes(text);

    builder.append(makeTermBold(textWithReplacedQuotes,term))
            .append(font_close);

    return builder.toString();
  }

  private static String replaceQuotes(String text){
    return text.replace("'", "`");
  }

  private static final String bold_open = "<b>";
  private static final String bold_close = "</b>";

  private static String makeTermBold(String text, String term) {
    return text.replaceAll("(?i)" + term, bold_open + term.toUpperCase() + bold_close);
  }
}