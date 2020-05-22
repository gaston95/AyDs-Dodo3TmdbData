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
import java.net.URL;
import java.util.Iterator;

public class OtherInfoWindow {
  private JPanel contentPane;
  private JTextPane descriptionTextPane;
  private JPanel imagePanel;

  private static final String movie_in_db = "[*]";

  public void getMoviePlot(OmdbMovie movie) {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

    TheMovieDBAPI tmdbAPI = retrofit.create(TheMovieDBAPI.class);

    descriptionTextPane.setContentType("text/html");

    // this is needed to open a link in the browser
    descriptionTextPane.addHyperlinkListener(e -> {
      if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
        System.out.println(e.getURL());
        Desktop desktop = Desktop.getDesktop();
        try {
          desktop.browse(e.getURL().toURI());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });

    new Thread(new Runnable() {
      @Override
      public void run() {

        String text = DataBase.getOverview(movie.getTitle());

        String path = DataBase.getImageUrl(movie.getTitle());

        if (movieExistsInDb(text,path)) {
          text = movie_in_db + text;
        } else {
          Response<String> callResponse;
          try {
            text = "Description not found";
            callResponse = tmdbAPI.getTerm(movie.getTitle()).execute();

            System.out.println("JSON " + callResponse.body());

            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(callResponse.body(), JsonObject.class);

            Iterator<JsonElement> resultIterator = jobj.get("results").getAsJsonArray().iterator();

            JsonObject result = null;

            boolean movieFound = false;

            while (resultIterator.hasNext()) {
              result = resultIterator.next().getAsJsonObject();

              JsonElement yearJson = result.get("release_date");
              String year = yearJson == null ? "" : yearJson.getAsString().split("-")[0];

              if (year.equals(movie.getYear())) {
                movieFound = true;
                break;
              }
            }

            JsonElement extract = null;
            JsonElement backdropPathJson = null;
            JsonElement posterPath = null;

            if(result != null && movieFound){
              extract = result.get("overview");
              backdropPathJson = result.get("backdrop_path");
              posterPath = result.get("poster_path");
            }

            String backdropPath = null;

            System.out.println("backdropPathJson " + backdropPathJson);

            if (backdropPathJson != null && !backdropPathJson.isJsonNull()) {
              backdropPath =  backdropPathJson.getAsString();
            }

            if (extract == null || extract.isJsonNull()) {
              text = "No Results";
              path = "https://www.themoviedb.org/assets/2/v4/logos/256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png";
            } else {
              text = extract.getAsString().replace("\\n", "\n");
              text = textToHtml(text, movie.getTitle());

              if(posterPath != null && !posterPath.isJsonNull())
                text+="\n" + "<a href=https://image.tmdb.org/t/p/w400/" + posterPath.getAsString() +">View Movie Poster</a>";

              if(backdropPath != null)
                path = "https://image.tmdb.org/t/p/w400/" + backdropPath;

              if (path == null) {
                path = "https://www.themoviedb.org/assets/2/v4/logos/256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png";
              }

              // save to DB  <o/
              DataBase.saveMovieInfo(movie.getTitle(), text, path);
            }
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        }

        descriptionTextPane.setText(text);

        // set image
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }

        try {
          System.out.println("Get Image from " + path);
          URL url = new URL(path);
          BufferedImage image = ImageIO.read(url);
          System.out.println("Load image into frame...");
          JLabel label = new JLabel(new ImageIcon(image));
          imagePanel.add(label);

          // Refresh panel
          contentPane.validate();
          contentPane.repaint();

        } catch (Exception exp) {
          exp.printStackTrace();
        }

      }
    }).start();
  }

  private boolean movieExistsInDb(String text, String path) {
    return text != null && path != null;
  }
  public static void open(OmdbMovie movie) {
    OtherInfoWindow win = createWindow();

    win.getMoviePlot(movie);
  }

  private static OtherInfoWindow createWindow(){
    OtherInfoWindow win = new OtherInfoWindow();

    win.contentPane = new JPanel();
    win.contentPane.setLayout(new BoxLayout(win.contentPane, BoxLayout.PAGE_AXIS));
    win.contentPane.add(new JLabel("Data from The Movie Data Base"));

    win.imagePanel = new JPanel();

    JPanel descriptionPanel = new JPanel(new BorderLayout());
    win.descriptionTextPane = new JTextPane();
    win.descriptionTextPane.setEditable(false);
    win.descriptionTextPane.setContentType("text/html");
    win.descriptionTextPane.setMaximumSize(new Dimension(600, 400));
    descriptionPanel.add(win.descriptionTextPane);
    win.contentPane.add(descriptionPanel);
    win.contentPane.add(win.imagePanel);

    JFrame frame = new JFrame("Movie Info Dodo");
    frame.setMinimumSize(new Dimension(600, 600));
    frame.setContentPane(win.contentPane);
    frame.pack();
    frame.setVisible(true);

    DataBase.createNewDatabase();
    return win;
  }

  private static final String html_open = "<html>";
  private static final String body_open_style = "<body style=\"width: 400px\">";
  private static final String font_open_arial = "<font face=\"arial\">";
  private static final String font_close = "</font>";

  public static String textToHtml(String text, String term) {

    StringBuilder builder = new StringBuilder();
    builder.append(html_open + body_open_style)
            .append(font_open_arial);

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