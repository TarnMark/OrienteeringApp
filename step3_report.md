# Step 3 report
## Development Process
During this step, a lot of initial app design was refined and upgraded. Functionality was implemented to bring the application closer to the desired goal. Particularly, the Map screen was updated to go with the map and weather add-ons.

## Chosen API
The Open-Meteo API was chosen for this project. It is a free, open-source, and no-authentication-required weather API that provides global weather data. It is ideal for mobile applications because it allows fast queries with minimal setup and offers a variety of weather variables (temperature, precipitation, wind speed, etc.).

Also OSMDroid was used for displaying the world map. OSMDroid was chosen because it is a free, open-source map which can also be used offline.

## Example API Endpoint
Example endpoint used to fetch current weather data for a specific location (latitude and longitude provided dynamically from map interaction):
```
https://api.open-meteo.com/v1/forecast?latitude=58.3776&longitude=26.7290&current_weather=true
```
In the app, this request is triggered when the user long-presses on the map, passing the coordinates of that point to the `WeatherViewModel`, which calls the repository to fetch weather data.

## Error Handling Strategy
To ensure a user-friendly experience, the application implements error handling primarily within the `WeatherViewModel`, which is responsible for fetching and displaying weather data.

All network calls to the Open-Meteo API are wrapped in a `try–catch` block inside a coroutine launched with `viewModelScope`. This ensures that any exceptions thrown during the API request — such as connectivity issues, timeouts, or unexpected server responses — are caught and handled gracefully without crashing the application.

If such an issue occurs, the user is informed with the message "Weather unavailable".
