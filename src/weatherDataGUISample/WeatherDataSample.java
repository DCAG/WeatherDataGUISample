/*
 * Amir Granot,
 * H.I.T. 2015 Summer
 * Programming within the Internet environment.
 */
package weatherDataGUISample;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import weatherDataService.IWeatherDataService;
import weatherDataService.Location;
import weatherDataService.WeatherData;
import weatherDataService.WeatherDataServiceException;
import weatherDataService.WeatherDataServiceFactory;

public class WeatherDataSample extends JFrame{

	private static final long serialVersionUID = 90999406660890838L;

	private static WeatherDataSample thisWindow;// = this;
	
	private String home; //save location as home
	
	//WeatherDataService JAR implementation:
	private IWeatherDataService openWeatherMapService; //the service
	private WeatherData weatherData;  // the object data to store the results from the server.
	
	//Dark colours for the night
	private final Color nightBackgroundColor = new Color(51, 51, 102);
	private final Font  nightFont = new Font("MS Reference Sans Serif", Font.BOLD, 14);
	private final Color nightForegroundColor = Color.WHITE;
	private final Color nightSelectedTempUnit = Color.ORANGE;

	//Light colours for the day
	private final Color dayBackgroundColor = new Color(51, 51, 102);
	private final Font  dayFont = new Font("MS Reference Sans Serif", Font.BOLD, 14);
	private final Color dayForegroundColor = Color.BLACK;
	private final Color daySelectedTempUnit = Color.ORANGE;
	
	//the current colours:
	private Color currentBackgroundColor;
	private Font  currentFont;
	private Color currentForegroundColor;
	private Color currentSelectedTempUnit;
	private int temperatureUnits;
	private boolean isDark;//mainly to set the dark theme.
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					thisWindow = new WeatherDataSample();
					thisWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WeatherDataSample() {
		initializeInternal(); //initialisation of variables (listed up) that are not connected directly to the GUI.
		initialize(); // initialisation of GUI components
	}

	private void initializeInternal()
	{
		openWeatherMapService = WeatherDataServiceFactory.getWeatherDataService(WeatherDataServiceFactory.OPEN_WEATHER_MAP);
		
		isDark = true;
		setDayLightColors();
		
		home = "";
		temperatureUnits = WeatherData.TEMPERATURE_KELVIN_DEGREES;
	}
	
	private void setDayLightColors()
	{
		if(isDark)
		{
			currentBackgroundColor = nightBackgroundColor;
			currentFont = nightFont;
			currentForegroundColor = nightForegroundColor;
			currentSelectedTempUnit = nightSelectedTempUnit;
		}
		else
		{
			currentBackgroundColor = dayBackgroundColor;
			currentFont = dayFont;
			currentForegroundColor = dayForegroundColor;
			currentSelectedTempUnit = daySelectedTempUnit;
		}
	}
	
	/*
	 * GUI Events:
	 */
	
	MouseListener onBtnSearchCityClick = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			try {
				String city = txtSearchCity.getText();
				weatherData = openWeatherMapService.getWeatherData(new Location(city));
				//System.out.println(weatherData);
				fillInWeatherData();
			} catch (WeatherDataServiceException e) {
				JOptionPane.showMessageDialog(thisWindow, "Error occured during while trying to retrieve data from the server:\n"
						+ e.getMessage());
				//e.printStackTrace();
			}
		}
	};
	
	MouseAdapter onClickChangeMeasure = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if(weatherData == null || weatherData.getTemperature() == null)
				return;
			
			if(arg0.getSource().equals(btnKelvin))
			{
				temperatureUnits = WeatherData.TEMPERATURE_KELVIN_DEGREES;
				btnKelvin.setForeground(currentSelectedTempUnit);
				btnCelcius.setForeground(currentForegroundColor);
				btnFehrenheit.setForeground(currentForegroundColor);
				lblTemperatureValue.setText((new DecimalFormat("###.##")).format(weatherData.getTemperatureDegrees(temperatureUnits))+"\u00B0");
			}
			else if(arg0.getSource().equals(btnCelcius))
			{
				temperatureUnits = WeatherData.TEMPERATURE_CELCIUS_DEGREES;
				btnCelcius.setForeground(currentSelectedTempUnit);
				btnFehrenheit.setForeground(currentForegroundColor);
				btnKelvin.setForeground(currentForegroundColor);
				lblTemperatureValue.setText((new DecimalFormat("###.##")).format(weatherData.getTemperatureDegrees(temperatureUnits))+"\u00B0");
			}
			else if(arg0.getSource().equals(btnFehrenheit))
			{
				temperatureUnits = WeatherData.TEMPERATURE_FAHRENHEIT_DEGREES;
				btnFehrenheit.setForeground(currentSelectedTempUnit);
				btnKelvin.setForeground(currentForegroundColor);
				btnCelcius.setForeground(currentForegroundColor);
				lblTemperatureValue.setText((new DecimalFormat("###.##")).format(weatherData.getTemperatureDegrees(temperatureUnits))+"\u00B0");
			}
		}
	};
	
	MouseListener onBtnHomeClick = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if(home == "")
				return;
			try {
				weatherData = openWeatherMapService.getWeatherData(new Location(home));
				fillInWeatherData();
			} catch (WeatherDataServiceException e1) {
				JOptionPane.showMessageDialog(thisWindow, "Error occured during while trying to retrieve data from the server:\n"
						+ "Sent: " + home + ",\n"
						+ e1.getMessage());
				//e1.printStackTrace();
			}
		}
	};
	
	
	MouseListener onBtnSetHomeClick = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if(weatherData == null || (weatherData.getCityName() == null & weatherData.getCountryCode() == null))
				return;
			home = weatherData.getCityName()+","+weatherData.getCountryCode();
			btnHome.setText(weatherData.getCityName()+", "+weatherData.getCountryCode());
			btnHome.setVisible(true);
		}
	};
	
	private void fillInWeatherData()
	{
		//lastUpdated
		DateFormat fullDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		StringBuilder lastUpdated = new StringBuilder("Last Update: ");
		if(weatherData.getMeasureTimeCalendar()==null)
			lastUpdated.append("unknown");
		else
			lastUpdated.append(fullDateFormat.format(weatherData.getMeasureTimeCalendar().getTime()));
		this.lblLastUpdated.setText(lastUpdated.toString());
		
		//Location
		if((weatherData.getCityName()!=null & weatherData.getCountryCode()!=null) || (weatherData.getCoordinateLatitude() !=null & weatherData.getCoordinateLongitude()!=null))
		{
			pnlLocation.setVisible(true);
			this.lblCityname.setText(weatherData.getCityName()+", "+weatherData.getCountryCode());
			this.lblCoords.setText("Latitude:"+weatherData.getCoordinateLatitude()+",Longitude:"+weatherData.getCoordinateLongitude());
		}
		else
			pnlLocation.setVisible(false);
		
		
		//DayAndNight
		if(weatherData.getSunriseDate() !=null & weatherData.getSunsetDate()!=null)
		{
			pnlDayNight.setVisible(true);
			DateFormat miniDateFormat = new SimpleDateFormat("d.M.yy HH:mm");
			this.lblDayNight.setText("<html>Sunrise at:<br/>"+miniDateFormat.format(weatherData.getSunriseDate().getTime())+"<br />Sunset at:<br/>"+miniDateFormat.format(weatherData.getSunsetDate().getTime())+"<html>");
			
			StringBuilder timeToSunriseSunset = new StringBuilder("<html>Time to ");
			Long total = (long)1;
			if(weatherData.isDark())
			{
				this.lblDayNight.setIcon(new ImageIcon(WeatherDataSample.class.getResource("/Images/Weather-Moon-iconWhiteSmall.png")));
				timeToSunriseSunset.append("sunrise: ");
				total = weatherData.getSunrise() - weatherData.getSunset();
			}
			else
			{
				this.lblDayNight.setIcon(new ImageIcon(WeatherDataSample.class.getResource("/Images/Weather-Sun-iconWhiteSmall.png")));
				timeToSunriseSunset.append("sunset: ");
				total = weatherData.getSunset() - weatherData.getSunrise();
			}
			
			this.progressBar.setValue((int)(100*weatherData.getTimeToDaylightChange().toMillis()/(1000*total)));
			
			timeToSunriseSunset.append(weatherData.getTimeToDaylightChange().toMinutes());
			timeToSunriseSunset.append("min<br /><br /></html>");
			this.lblSunriseSunset.setText(timeToSunriseSunset.toString());
			
			
		}
		else
			pnlDayNight.setVisible(false);
		
		//Temperature
		if(weatherData.getTemperature()!=null)
		{
			pnlTemperature.setVisible(true);
			lblTemperatureValue.setText((new DecimalFormat("###.##")).format(weatherData.getTemperatureDegrees(temperatureUnits))+"\u00B0");
		}
		else
			pnlTemperature.setVisible(false);
		
		//Weather Description
		if(!weatherData.getWeatherDescription().isEmpty())
		{
			pnlWeatherDescription.setVisible(true);
			StringBuilder weatherDescription = new StringBuilder("<html><br />");
			for(String s:weatherData.getWeatherDescription())
				weatherDescription.append(s+"<br />");
			weatherDescription.append("</html>");
			this.lblWeatherDescription.setText(weatherDescription.toString());
		}
		else
			pnlWeatherDescription.setVisible(false);
		
		//Wind
		if(weatherData.getWindDirection()!=null)
		{
			lblWindDirection.setVisible(true);
			lblWindDirection.rotateImage(weatherData.getWindDirection(), this);
		}
		else
			lblWindDirection.setVisible(false);
		
		if(weatherData.getWindSpeed()!=null)
		{
			lblWindSpeed.setVisible(true);
			lblWindSpeed.setText(weatherData.getWindSpeed() + "m/s");
		}
		else
			lblWindSpeed.setVisible(false);
		
		pnlWind.setVisible(lblWindDirection.isVisible() | lblWindSpeed.isVisible());
		
		//Humidity
		if(weatherData.getHumidity()!=null)
		{	
			pnlHumidity.setVisible(true);
			lblHumidityValue.setText(weatherData.getHumidity() + "%");
		}
		else
			pnlHumidity.setVisible(false);
		
		//Cloudiness
		if(weatherData.getCloudiness() !=null)
		{
			pnlCloudness.setVisible(true);
			lblCloudnessValue.setText(weatherData.getCloudiness() + "%");
		}
		else
			pnlCloudness.setVisible(false);
		
		//Rain
		if(weatherData.getRainVolume()!=null)
		{
			pnlRain.setVisible(true);
			lblRainValue.setText(""+weatherData.getCloudiness());
		}
		else
			pnlRain.setVisible(false);
		
		//Snow
		if(weatherData.getSnowVolume()!=null)
		{
			pnlSnow.setVisible(true);
			lblSnowValue.setText(""+weatherData.getSnowVolume());
		}
		else
			pnlSnow.setVisible(false);
	}//fillInWeatherData() END
	
	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize() {

		btnHome = new JButton();
		txtSearchCity = new JTextField();
		btnSearchCity = new JButton();
		pnlSearchHome = new JPanel();
		lblLastUpdated = new JLabel();
		btnSetHome = new JButton();
		pnlUpdateSetHome = new JPanel();
		lblCityname = new JLabel();
		lblCoords = new JLabel();
		pnlLocation = new JPanel();
		lblDayNight = new JLabel();
		lblSunriseSunset = new JLabel();
		progressBar = new JProgressBar();
		pnlSunriseSunset = new JPanel();
		pnlDayNight = new JPanel();
		btnCelcius = new JButton();
		btnFehrenheit = new JButton();
		btnKelvin = new JButton();
		lblTemperatureValue = new JLabel();
		pnlTemperature = new JPanel();
		pnlMeasurements = new JPanel();
		lblWeatherDescription = new JLabel();
		pnlWeatherDescription = new JPanel();
		lblWindDirection = new RotatingImageLabel(); //notice! this is not a regular JLabel.
		lblWindSpeed = new JLabel();
		pnlWind = new JPanel();
		lblHumidityValue = new JLabel();
		pnlHumidity = new JPanel();
		lblCloudnessValue = new JLabel();
		pnlCloudness = new JPanel();
		lblRainValue = new JLabel();
		pnlRain = new JPanel();
		lblSnowValue = new JLabel();
		pnlSnow = new JPanel();
		pnlWeatherData = new JPanel();
		
		//
		//WeatherDataSample (Form) begin
		//
		this.setBounds(100, 100, 600, 600);
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.getContentPane().setBackground(new Color(25, 25, 112));
		this.getContentPane().setForeground(new Color(135, 206, 250));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//header - search and home city
		
		//
		//btnHome
		//
		//btnHome.setText("             ");
		btnHome.setForeground(new Color(255, 215, 0));
		btnHome.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 20));
		btnHome.setIcon(new ImageIcon(WeatherDataSample.class.getResource("/Images/star-iconLowRes.png")));
		btnHome.setContentAreaFilled(false);
		btnHome.addMouseListener(onBtnHomeClick);
		btnHome.setVisible(false);
		
		//
		//txtSearchCity
		//
		txtSearchCity.setHorizontalAlignment(SwingConstants.CENTER);
		txtSearchCity.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 20));
		txtSearchCity.setColumns(10);
		
		//
		//btnSearchCity
		//
		btnSearchCity.setIcon(new ImageIcon(WeatherDataSample.class.getResource("/Images/magnifying-glass-icon64.png")));
		btnSearchCity.setContentAreaFilled(false);
		btnSearchCity.addMouseListener(onBtnSearchCityClick);
		
		//
		//pnlSearchHome
		//
		pnlSearchHome.setBorder(null);
		pnlSearchHome.setBackground(new Color(70, 130, 180));
		int searchHomeHeight = (int) (Math.round(this.getBounds().getHeight() * 0.08));
		pnlSearchHome.setPreferredSize(new Dimension((int)this.getBounds().getWidth(),searchHomeHeight));
		pnlSearchHome.setMinimumSize(new Dimension(Integer.MAX_VALUE,searchHomeHeight));
		pnlSearchHome.setMaximumSize(new Dimension(Integer.MAX_VALUE,searchHomeHeight));
		pnlSearchHome.setLayout(new BoxLayout(pnlSearchHome, BoxLayout.X_AXIS));
		pnlSearchHome.add(btnHome);
		pnlSearchHome.add(txtSearchCity);
		pnlSearchHome.add(btnSearchCity);
		
		//last update time and set as home
		
		//
		//lblLastUpdated
		//
		lblLastUpdated.setText("Last Update:");
		lblLastUpdated.setHorizontalAlignment(SwingConstants.LEFT);
		lblLastUpdated.setForeground(currentForegroundColor);
		lblLastUpdated.setBounds(67, 11, 182, 15);
		lblLastUpdated.setFont(nightFont);
		
		//
		//btnSetHome
		//
		btnSetHome.setSelectedIcon(new ImageIcon(WeatherDataSample.class.getResource("/Images/star-iconLowRes.png")));
		btnSetHome.setIconTextGap(0);
		btnSetHome.setBorderPainted(false);
		btnSetHome.setAlignmentX(Component.RIGHT_ALIGNMENT);
		btnSetHome.setHorizontalAlignment(SwingConstants.RIGHT);
		btnSetHome.setRolloverIcon(new ImageIcon(WeatherDataSample.class.getResource("/Images/star-iconLowResSelected.png")));
		btnSetHome.setIcon(new ImageIcon(WeatherDataSample.class.getResource("/Images/star-iconLowRes.png")));
		btnSetHome.setContentAreaFilled(false);
		btnSetHome.addMouseListener(onBtnSetHomeClick);
		
		//
		//pnlUpdateSetHome
		//
		pnlUpdateSetHome.setBackground(currentBackgroundColor);
		int updateSetHomeHeight = (int) (Math.round(this.getBounds().getHeight() * 0.08));
		pnlUpdateSetHome.setPreferredSize(new Dimension((int)this.getBounds().getWidth(), updateSetHomeHeight));
		pnlUpdateSetHome.setMinimumSize(new Dimension(Integer.MAX_VALUE,updateSetHomeHeight));
		pnlUpdateSetHome.setMaximumSize(new Dimension(Integer.MAX_VALUE,updateSetHomeHeight));
		pnlUpdateSetHome.setLayout(new BorderLayout(0, 0));
		pnlUpdateSetHome.add(lblLastUpdated);
		pnlUpdateSetHome.add(btnSetHome, BorderLayout.EAST);

		//
		//lblCityname
		//
		//lblCityname.setText("CityName, CC");
		lblCityname.setHorizontalAlignment(SwingConstants.CENTER);
		lblCityname.setBounds(112, 37, 243, 44);
		lblCityname.setFont(new Font("MS Reference Sans Serif", Font.PLAIN, 35));
		lblCityname.setForeground(currentForegroundColor);
		lblCityname.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//
		//lblCoords
		//
		//lblCoords.setText("Latitude:00.000,Longitude:00.000");
		lblCoords.setHorizontalAlignment(SwingConstants.CENTER);
		lblCoords.setBounds(67, 117, 288, 20);
		lblCoords.setFont(currentFont);
		lblCoords.setForeground(new Color(230, 230, 250));
		lblCoords.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//
		//pnlLocation
		//
		pnlLocation.setBackground(currentBackgroundColor);
		pnlLocation.setLayout(new BoxLayout(pnlLocation, BoxLayout.Y_AXIS));
		pnlLocation.add(lblCityname);//1
		pnlLocation.add(lblCoords);//2
		pnlLocation.setVisible(false);
		
		//
		//lblDayNight
		//
		//lblDayNight.setText("<html>Sunrise at: 9h<br />Sunset at: 15h</html>");
		//lblDayNight.setIcon();
		lblDayNight.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 14));
		lblDayNight.setForeground(currentForegroundColor);
		
		//
		//lblSunriseSunset
		//
		//lblSunriseSunset.setText("<html>Time to sunrise: 2h<br /><br /><html>");
		lblSunriseSunset.setFont(currentFont);
		lblSunriseSunset.setForeground(currentForegroundColor);
		
		//
		//progressBar
		//
		progressBar.setFont(currentFont);
		progressBar.setValue(0);
		progressBar.setForeground(Color.ORANGE);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(100, 50));
		
		//
		//pnlSunriseSunset
		//
		pnlSunriseSunset.setLayout(new BoxLayout(pnlSunriseSunset, BoxLayout.Y_AXIS));
		pnlSunriseSunset.setBackground(currentBackgroundColor);
		pnlSunriseSunset.setPreferredSize(new Dimension(500, 100));
		pnlSunriseSunset.add(lblSunriseSunset);
		pnlSunriseSunset.add(progressBar);
		
		//
		//pnlDayNight
		//
		pnlDayNight.setPreferredSize(new Dimension(550, 100));
		pnlDayNight.setBounds(new Rectangle(10, 10, 10, 10));
		pnlDayNight.setBorder(new TitledBorder(new LineBorder(currentForegroundColor, 0, true), "Time of day", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
		pnlDayNight.setBackground(currentBackgroundColor);
		pnlDayNight.setLayout(new GridLayout(1, 2, 0, 0));
		pnlDayNight.add(lblDayNight);
		pnlDayNight.add(pnlSunriseSunset);
		pnlDayNight.setVisible(false);
		
		//
		//btnCelcius
		//
		btnCelcius.setText("C");
		btnCelcius.setForeground(currentForegroundColor);
		btnCelcius.setBackground(currentBackgroundColor);
		btnCelcius.setFont(nightFont);
		btnCelcius.addMouseListener(onClickChangeMeasure);
		
		//
		//btnFehrenheit
		//
		btnFehrenheit.setText("F");
		btnFehrenheit.setForeground(currentForegroundColor);
		btnFehrenheit.setBackground(currentBackgroundColor);
		btnFehrenheit.setFont(nightFont);
		btnFehrenheit.addMouseListener(onClickChangeMeasure);
		
		//
		//btnKelvin
		//
		btnKelvin.setText("K");
		btnKelvin.setForeground(currentSelectedTempUnit);
		btnKelvin.setBackground(currentBackgroundColor);
		btnKelvin.setFont(nightFont);
		btnKelvin.addMouseListener(onClickChangeMeasure);
		
		//
		//pnlMeasurements
		//
		pnlMeasurements.setBackground(currentBackgroundColor);
		pnlMeasurements.setLayout(new GridLayout(3, 1, 0, 0));
		pnlMeasurements.add(btnCelcius);//1
		pnlMeasurements.add(btnFehrenheit);//2
		pnlMeasurements.add(btnKelvin);//3
		
		//
		//lblTemperatureValue
		//
		lblTemperatureValue.setText("0\u00B0");
		lblTemperatureValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblTemperatureValue.setForeground(currentForegroundColor);
		lblTemperatureValue.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 32));
		
		//
		//pnlTemperature
		//
		pnlTemperature.setBorder(new TitledBorder(new LineBorder(currentForegroundColor, 0, true), "Temperature", TitledBorder.LEADING, TitledBorder.TOP, null, Color.WHITE));
		pnlTemperature.setPreferredSize(new Dimension(200, 100));
		pnlTemperature.setBackground(currentBackgroundColor);
		pnlTemperature.setLayout(new BorderLayout(0, 0));
		pnlTemperature.add(lblTemperatureValue, BorderLayout.CENTER);//1
		pnlTemperature.add(pnlMeasurements, BorderLayout.EAST);//2
		pnlTemperature.setVisible(false);
		
		//
		//lblWeatherDescription
		//
		lblWeatherDescription.setText("<html></html>");
		lblWeatherDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblWeatherDescription.setHorizontalAlignment(SwingConstants.CENTER);
		lblWeatherDescription.setForeground(currentForegroundColor);
		lblWeatherDescription.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 20));
		
		//
		//pnlWeatherDescription
		//
		pnlWeatherDescription.setBorder(new TitledBorder(new LineBorder(currentForegroundColor, 0, true), "Today's Weather", TitledBorder.LEADING, TitledBorder.TOP, null, Color.WHITE));
		pnlWeatherDescription.setPreferredSize(new Dimension(300, 100));
		pnlWeatherDescription.setBackground(currentBackgroundColor);
		pnlWeatherDescription.setLayout(new BoxLayout(pnlWeatherDescription, BoxLayout.Y_AXIS));
		pnlWeatherDescription.add(lblWeatherDescription);//1
		pnlWeatherDescription.setVisible(false);
		
		//
		//lblWindDirection
		//
		lblWindDirection.setImg((new ImageIcon(WeatherDataSample.class.getResource("/Images/Arrows-Down-icon.png"))).getImage());
		
		//
		//lblWindSpeed
		//
		lblWindSpeed.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 20));
		lblWindSpeed.setForeground(currentForegroundColor);
		
		//
		//pnlWind
		//
		pnlWind.setBorder(new TitledBorder(new LineBorder(currentForegroundColor, 0, true), "Wind", TitledBorder.LEADING, TitledBorder.TOP, null, Color.WHITE));
		pnlWind.setPreferredSize(new Dimension(120, 100));
		pnlWind.setBackground(currentBackgroundColor);
		pnlWind.setLayout(new BoxLayout(pnlWind, BoxLayout.X_AXIS));
		pnlWind.add(lblWindDirection);//1
		pnlWind.add(lblWindSpeed);//2
		pnlWind.setVisible(false);
		
		//
		//lblHumidityValue
		//
		lblHumidityValue.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblHumidityValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblHumidityValue.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 20));
		lblHumidityValue.setForeground(currentForegroundColor);
		
		//
		//pnlHumidity
		//
		pnlHumidity.setBorder(new TitledBorder(new LineBorder(currentForegroundColor, 0, true), "Humidity", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
		pnlHumidity.setPreferredSize(new Dimension(100, 100));
		pnlHumidity.setBackground(currentBackgroundColor);
		pnlHumidity.setLayout(new BoxLayout(pnlHumidity, BoxLayout.X_AXIS));
		pnlHumidity.add(lblHumidityValue);
		pnlHumidity.setVisible(false);
		
		//
		//lblCloudnessValue
		//
		lblCloudnessValue.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblCloudnessValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblCloudnessValue.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 20));
		lblCloudnessValue.setForeground(currentForegroundColor);
		
		//
		//pnlCloudness
		//
		pnlCloudness.setBorder(new TitledBorder(new LineBorder(currentForegroundColor, 0, true), "Cloudness", TitledBorder.LEADING, TitledBorder.TOP, null, Color.WHITE));
		pnlCloudness.setPreferredSize(new Dimension(100, 100));
		pnlCloudness.setBackground(currentBackgroundColor);
		pnlCloudness.setLayout(new BoxLayout(pnlCloudness, BoxLayout.X_AXIS));
		pnlCloudness.add(lblCloudnessValue);
		pnlCloudness.setVisible(false);
		
		//
		//lblRainValue
		//
		lblRainValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblRainValue.setForeground(currentForegroundColor);
		lblRainValue.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 20));
		
		//
		//pnlRain
		//
		pnlRain.setBorder(new TitledBorder(new LineBorder(currentForegroundColor, 0, true), "Rain volume", TitledBorder.LEADING, TitledBorder.TOP, null, Color.WHITE));
		pnlRain.setPreferredSize(new Dimension(100, 100));
		pnlRain.setBackground(currentBackgroundColor);
		pnlRain.setLayout(new BoxLayout(pnlRain, BoxLayout.X_AXIS));
		pnlRain.add(lblRainValue);
		pnlRain.setVisible(false);
		
		//
		//lblSnowValue
		//
		//lblSnowValue.setText("0.000");
		lblSnowValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblSnowValue.setForeground(currentForegroundColor);
		lblSnowValue.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 20));
		
		//
		//pnlSnow
		//
		pnlSnow.setPreferredSize(new Dimension(100, 100));
		pnlSnow.setBorder(new TitledBorder(new LineBorder(currentForegroundColor, 0, true), "Snow volume", TitledBorder.LEADING, TitledBorder.TOP, null, Color.WHITE));
		pnlSnow.setBackground(currentBackgroundColor);
		pnlSnow.setLayout(new BoxLayout(pnlSnow, BoxLayout.X_AXIS));
		pnlSnow.add(lblSnowValue);
		pnlSnow.setVisible(false);
		
		//main - weather data
		//
		//pnlWeatherData
		//
		pnlWeatherData.setBackground(currentBackgroundColor);
		int weatherDataHeight = (int) (Math.round(this.getBounds().getHeight() * 0.8));
		pnlWeatherData.setPreferredSize(new Dimension((int)this.getBounds().getWidth(), weatherDataHeight));
		pnlWeatherData.setMinimumSize(new Dimension(Integer.MAX_VALUE,weatherDataHeight));
		pnlWeatherData.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		pnlWeatherData.add(pnlLocation);//1
		pnlWeatherData.add(pnlDayNight);//2
		pnlWeatherData.add(pnlTemperature);//3
		pnlWeatherData.add(pnlWeatherDescription);//4
		pnlWeatherData.add(pnlWind);//5
		pnlWeatherData.add(pnlHumidity);//6
		pnlWeatherData.add(pnlCloudness);//7
		pnlWeatherData.add(pnlRain);//8
		pnlWeatherData.add(pnlSnow);//9
		
		
		//
		//WeatherDataSample (Form) end
		//
		this.getContentPane().add(pnlSearchHome);
		this.getContentPane().add(pnlUpdateSetHome);
		this.getContentPane().add(pnlWeatherData);
		
	}
	
	//Components:
	
	JButton btnHome;
	JTextField txtSearchCity;
	JPanel pnlSearchHome;
	JButton btnSearchCity;
	JLabel lblLastUpdated;
	JButton btnSetHome;
	JPanel pnlUpdateSetHome;
	JLabel lblCityname;
	JLabel lblCoords;
	JPanel pnlLocation;
	JLabel lblDayNight;
	JLabel lblSunriseSunset;
	JProgressBar progressBar;
	JPanel pnlSunriseSunset;
	JPanel pnlDayNight;
	JButton btnCelcius;
	JButton btnFehrenheit;
	JButton btnKelvin;
	JLabel lblTemperatureValue;
	JPanel pnlTemperature;
	JPanel pnlMeasurements;
	JLabel lblWeatherDescription;
	JPanel pnlWeatherDescription;
	//JLabel lblWindDirection;
	RotatingImageLabel lblWindDirection;
	JLabel lblWindSpeed;
	JPanel pnlWind;
	JLabel lblHumidityValue;
	JPanel pnlHumidity;
	JLabel lblCloudnessValue;
	JPanel pnlCloudness;
	JLabel lblRainValue;
	JPanel pnlRain;
	JLabel lblSnowValue;
	JPanel pnlSnow;
	JPanel pnlWeatherData;

}
