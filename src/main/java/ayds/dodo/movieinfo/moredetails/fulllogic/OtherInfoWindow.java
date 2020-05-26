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

  private static final String single_line_break = "\n";
  private static final String local_movie = "[*]";
  private static final String api_url = "https://api.themoviedb.org/3/";

  public static void open(OmdbMovie movie) {
    OtherInfoWindow win = createWindow();

    win.getMoviePlot(movie);
  }

  private static final String content_type = "text/html";
  private static final String frame_title = "Movie Info Dodo";
  private static final String label_text = "Data from The Movie Data Base";
  private static final int width = 600;
  private static final int height = 400;

  private static OtherInfoWindow createWindow() {
    OtherInfoWindow win = new OtherInfoWindow();

    win.contentPane = new JPanel();
    win.contentPane.setLayout(new BoxLayout(win.contentPane, BoxLayout.PAGE_AXIS));
    win.contentPane.add(new JLabel(label_text));

    win.imagePanel = new JPanel();
    win.contentPane.add(win.imagePanel);

    JPanel descriptionPanel = new JPanel(new BorderLayout());
    win.descriptionTextPane = new JTextPane();
    win.descriptionTextPane.setEditable(false);
    win.descriptionTextPane.setContentType(content_type);
    win.descriptionTextPane.setMaximumSize(new Dimension(width, height));
    descriptionPanel.add(win.descriptionTextPane);
    win.contentPane.add(descriptionPanel);

    JFrame frame = new JFrame(frame_title);
    frame.setMinimumSize(new Dimension(width, height));
    frame.setContentPane(win.contentPane);
    frame.pack();
    frame.setVisible(true);

    return win;
  }

  private static final String path_default = "https://www.themoviedb.org/assets/2/v4/logos/" +
          "256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png";
  private static final String path_url = "https://image.tmdb.org/t/p/w400/";
  private static final String link_open = "<a href=";
  private static final String link_close = "</a>";
  private static final String hyperlink_text = "View Movie Poster";

  public void getMoviePlot(OmdbMovie movie) {
    setHyperLinkListener();

    new Thread(() -> {

      DataBase.createNewDatabase();
      String text = DataBase.getOverview(movie.getTitle());
      String path = DataBase.getImageUrl(movie.getTitle());

      if (movieExistsInDb(text,path)) {
        text = getTextInDB(text);
      } else {

        text = "No Results";
        path = path_default;

        JsonObject searchResult = searchMovie(movie);
        if(searchResult != null){

          JsonElement extract = searchResult.get("overview");
          if (isNotNull(extract)) {
            text = extract.getAsString().replace("\\n", single_line_break);
            text = textToHtml(text, movie.getTitle());

            JsonElement backdropPathJson = searchResult.get("backdrop_path");
            if (isNotNull(backdropPathJson))
              path = path_url + backdropPathJson.getAsString();

            JsonElement posterPath = searchResult.get("poster_path");
            if(isNotNull(posterPath))
              text += single_line_break + link_open + path_url + posterPath.getAsString() +
                      ">" + hyperlink_text + link_close;

            DataBase.saveMovieInfo(movie.getTitle(), text, path);
          }
        }
      }
      descriptionTextPane.setText(text);
      setImage(path);
    }).start();
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

  private static String getTextInDB(String text) {
    return local_movie + text;
  }

  private boolean isNotNull(JsonElement element) {
    return element != null && !element.isJsonNull();
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

  private TheMovieDBAPI createAPI(){
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(api_url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

    return retrofit.create(TheMovieDBAPI.class);
  }

  private boolean areSameYear(JsonObject result, String movieYear) {
    JsonElement yearJson = result.get("release_date");
    String year = yearJson == null ? "" : yearJson.getAsString().split("-")[0];

    return year.equals(movieYear);
  }

  private void setImage(String path) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
            UnsupportedLookAndFeelException ignored) {
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
  private static final String bold_open = "<b>";
  private static final String bold_close = "</b>";

  private static String textToHtml(String text, String term) {

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

  private static String makeTermBold(String text, String term) {
    return text.replaceAll("(?i)" + term, bold_open + term.toUpperCase() + bold_close);
  }
}