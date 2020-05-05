package ayds.dodo.movieinfo.home.view;

import ayds.dodo.movieinfo.home.model.HomeModel;
import ayds.dodo.movieinfo.home.model.entities.OmdbMovie;
import ayds.observer.Observable;
import ayds.observer.Subject;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class MainWindow implements HomeView {
  private static final String WINDOW_TITLE = "Movies Info Dodo";

  private JTextField termTextField;
  private JButton searchButton;
  private JPanel contentPanel;
  private JTextPane descriptionPane;
  private JPanel posterPanel;
  private JButton modeDetailsButton;
  private JLabel posterImageLabel;

  private final HomeModel homeModel;
  private final MovieDescriptionHelper movieDescriptionHelper;

  private Subject<UiEvent> onActionSubject = new Subject<>();

  private final static String SPLASH_URL = "https://springfieldfiles.com/albums/books/0373.JPG";

  MainWindow(HomeModel homeModel, MovieDescriptionHelper movieDescriptionHelper) {

    this.homeModel = homeModel;
    this.movieDescriptionHelper = movieDescriptionHelper;

    buildUi();
    initUi();
    initListeners();
    initObservers();
  }

  @Override
  public void openView() {
    JFrame frame = new JFrame(WINDOW_TITLE);
    frame.setContentPane(this.contentPanel);
    frame.setMinimumSize(new Dimension(600, 800));
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  @NotNull
  @Override
  public String getMovieTitle() {
    return termTextField.getText();
  }

  @NotNull
  @Override
  public Observable<UiEvent> onUiEvent() {
    return onActionSubject;
  }

  private void buildUi() {
    contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

    JPanel searchTermPanel = new JPanel();
    searchTermPanel.setLayout(new BorderLayout());
    searchTermPanel.setMaximumSize(new Dimension(400, 12));
    termTextField = new JFormattedTextField();
    searchTermPanel.add(termTextField);
    contentPanel.add(searchTermPanel);

    JPanel searchButtonPanel = new JPanel();
    searchButtonPanel.setLayout(new BorderLayout());
    searchButtonPanel.setMaximumSize(new Dimension(400, 12));
    searchButton = new JButton("Search");
    searchButtonPanel.add(searchButton);
    contentPanel.add(searchButtonPanel);

    JPanel descriptionPanel = new JPanel();
    descriptionPane = new JTextPane();
    descriptionPane.setEditable(false);
    descriptionPane.setContentType("text/html");
    descriptionPane.setMaximumSize(new Dimension(600, 400));
    descriptionPanel.add(descriptionPane);
    contentPanel.add(descriptionPanel);

    JPanel moreDetailsButtonPanel = new JPanel();
    moreDetailsButtonPanel.setLayout(new BorderLayout());
    moreDetailsButtonPanel.setMaximumSize(new Dimension(400, 12));
    modeDetailsButton = new JButton("More Details");
    modeDetailsButton.setEnabled(false);
    moreDetailsButtonPanel.add(modeDetailsButton);
    descriptionPanel.add(moreDetailsButtonPanel);
    contentPanel.add(moreDetailsButtonPanel);

    posterPanel = new JPanel();
    contentPanel.add(posterPanel);

  }

  private void initUi() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
    }

    updateMoviePoster(SPLASH_URL);
  }

  private void initListeners() {
    searchButton.addActionListener(e -> {
      onActionSubject.notify(UiEvent.SEARCH_ACTION);
      modeDetailsButton.setEnabled(false);
    });

    modeDetailsButton.addActionListener(e -> onActionSubject.notify(UiEvent.MORE_DETAILS_ACTION));
  }

  private void initObservers() {
    homeModel.movieObservable().subscribe(this::updateMovieInfo);
  }

  private void updateMovieInfo(OmdbMovie movie) {
    updateMovieDescription(movie);
    updateMoviePoster(movie.getPosterUrl());
    enableMoreDetails(movie);
  }

  private void updateMovieDescription(OmdbMovie movie) {
    descriptionPane.setText(movieDescriptionHelper.getMovieDescriptionText(movie));
  }

  private void updateMoviePoster(String url) {
    cleanMoviePoster();
    BufferedImage image = getImageFromUrl(url);

    if (image != null) {
      setPosterImage(image);
      refreshPanel();
    }
  }

  private void cleanMoviePoster() {
    if (posterImageLabel != null) posterPanel.remove(posterImageLabel);
  }

  private BufferedImage getImageFromUrl(String imageUrl) {
    BufferedImage image = null;
    try {
      URL url = new URL(imageUrl);
      image = ImageIO.read(url);
    } catch (Exception e) {
      System.out.println("Could not get image from url " + imageUrl);
    }
    return image;
  }

  private void setPosterImage(BufferedImage image) {
    posterImageLabel = new JLabel(new ImageIcon(image));
    posterPanel.add(posterImageLabel);
  }

  private void refreshPanel() {
    contentPanel.validate();
    contentPanel.repaint();
  }

  private void enableMoreDetails(OmdbMovie movie) {
    modeDetailsButton.setEnabled(!movie.getTitle().isEmpty());
  }
}