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
  private JTextPane textPane2;
  private JPanel imagePanel;

  public void getMoviePlot(OmdbMovie movie) {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

    TheMovieDBAPI tmdbAPI = retrofit.create(TheMovieDBAPI.class);

    textPane2.setContentType("text/html");

    // this is needed to open a link in the browser
    textPane2.addHyperlinkListener(e -> {
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

        if (text != null && path != null) { // exists in db

          text = "[*]" + text;
        } else { // get from service
          Response<String> callResponse;
          try {
            callResponse = tmdbAPI.getTerm(movie.getTitle()).execute();

            System.out.println("JSON " + callResponse.body());


            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(callResponse.body(), JsonObject.class);

            Iterator<JsonElement> resultIterator = jobj.get("results").getAsJsonArray().iterator();

            JsonObject result = null;

            while (resultIterator.hasNext()) {
              result = resultIterator.next().getAsJsonObject();

              String year = result.get("release_date").getAsString().split("-")[0];

              if (year.equals(movie.getYear())) break;
            }


            JsonElement extract = result.get("overview");

            JsonElement backdropPathJson = result.get("backdrop_path");

            String backdropPath = null;

            System.out.println("backdropPathJson " + backdropPathJson);

            if (!backdropPathJson.isJsonNull()) {
              backdropPath =  backdropPathJson.getAsString();
            }


            JsonElement posterPath = result.get("poster_path");

            if (extract == null) {
              text = "No Results";
            } else {
              text = extract.getAsString().replace("\\n", "\n");
              text = textToHtml(text, movie.getTitle());

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

        textPane2.setText(text);


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

  public static void open(OmdbMovie movie) {

    OtherInfoWindow win = new OtherInfoWindow();

    win.contentPane = new JPanel();
    win.contentPane.setLayout(new BoxLayout(win.contentPane, BoxLayout.PAGE_AXIS));

    JLabel label = new JLabel();
    label.setText("Data from The Movie Data Base");
    win.contentPane.add(label);

    win.imagePanel = new JPanel();
    win.contentPane.add(win.imagePanel);

    JPanel descriptionPanel = new JPanel();
    win.textPane2 = new JTextPane();
    win.textPane2.setEditable(false);
    win.textPane2.setContentType("text/html");
    win.textPane2.setMaximumSize(new Dimension(600, 400));
    descriptionPanel.add( win.textPane2);
    win.contentPane.add(descriptionPanel);

    JFrame frame = new JFrame("Movie Info Dodo");
    frame.setMinimumSize(new Dimension(600, 600));
    frame.setContentPane(win.contentPane);
    frame.pack();
    frame.setVisible(true);

    DataBase.createNewDatabase();
    DataBase.saveMovieInfo("test", "sarasa", "");


    System.out.println(DataBase.getOverview("test"));
    System.out.println(DataBase.getOverview("nada"));


    win.getMoviePlot(movie);
  }

  public static String textToHtml(String text, String term) {

    StringBuilder builder = new StringBuilder("<html><body style='width: 400px'>");

    builder.append("<font face=\"arial\">");

    String textWithBold = text
            .replace("'", "`")
            .replaceAll("(?i)" + term, "<b>" + term.toUpperCase() + "</b>");

    builder.append(textWithBold);

    builder.append("</font>");

    return builder.toString();
  }

}
