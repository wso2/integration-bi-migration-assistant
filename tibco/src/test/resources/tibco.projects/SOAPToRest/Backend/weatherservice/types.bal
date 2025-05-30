type WeatherRequest record {|
    string latitude;
    string longitude;
|};

type WeatherResponse record {|
    decimal temperature;
    decimal windSpeed;
    decimal humidity;
|};
