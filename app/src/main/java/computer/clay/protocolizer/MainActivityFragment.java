package computer.clay.protocolizer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

<<<<<<< HEAD:app/src/main/java/computer/clay/sculptor/sculptor/MainActivityFragment.java
    private ArrayAdapter<String> httpRequestAdapter;

    private ArrayList<String> behaviorSequence = new ArrayList<String>();
=======
    private Communication communication = null;
    ArrayAdapter<String> listAdapter;
>>>>>>> origin/master:app/src/main/java/computer/clay/protocolizer/MainActivityFragment.java

    public MainActivityFragment() {
    }

    @Override
    public void onPause() {
        super.onPause();
        communication.stopDatagramServer();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (communication == null) {
            communication = new Communication();
            communication.startDatagramServer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (communication == null) {
            communication = new Communication();
            communication.startDatagramServer();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        if (this.communication == null) {
            Log.e ("Clay", "Communication subsystem does not exist in memory.");
        }

        // Define the data
<<<<<<< HEAD:app/src/main/java/computer/clay/sculptor/sculptor/MainActivityFragment.java
        behaviorSequence.add("create");
=======
//        ArrayList<String> httpRequests = new ArrayList<String>();

//        httpRequests.add("1");
//        httpRequests.add("2");
//        httpRequests.add("3");
//        httpRequests.add("4");
//        httpRequests.add("5");
//        httpRequests.add("6");
//        httpRequests.add("7");
//        httpRequests.add("8");
//        httpRequests.add("9");
//        httpRequests.add("10");
//        httpRequests.add("11");
//        httpRequests.add("12");

//        httpRequests.add("turn light 1 on"); // GET /message?content=turn%201%20on
//        httpRequests.add("turn light 1 off");
//        httpRequests.add("turn light 2 on"); // GET /message?content=turn%201%20on
//        httpRequests.add("turn light 2 off");
//        httpRequests.add("turn light 3 on"); // GET /message?content=turn%201%20on
//        httpRequests.add("turn light 3 off");
//        httpRequests.add("turn light 4 on"); // GET /message?content=turn%201%20on
//        httpRequests.add("turn light 4 off");
//        httpRequests.add("turn light 5 on"); // GET /message?content=turn%201%20on
//        httpRequests.add("turn light 5 off");
//        httpRequests.add("turn light 6 on"); // GET /message?content=turn%201%20on
//        httpRequests.add("turn light 6 off");

//        httpRequests.add("GET /channels");
//        httpRequests.add("POST /channel/1");
//        httpRequests.add("GET /experience"); // i.e., this is rather than the memory, store, or database
//        httpRequests.add("GET /behavior");
>>>>>>> origin/master:app/src/main/java/computer/clay/protocolizer/MainActivityFragment.java

        // communication.getUnits().add("N/A");

        // Define the adapter (adapts the data to the actual rendered view)
        listAdapter = new ArrayAdapter<String>( // ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this fragment's parent activity).
                R.layout.list_item_http_request, // ID of list item layout
                R.id.list_item_http_request_textview, // ID of textview to populate (using the specified list item layout)
<<<<<<< HEAD:app/src/main/java/computer/clay/sculptor/sculptor/MainActivityFragment.java
                behaviorSequence // The list of forecast data
=======
                this.communication.getUnits() // httpRequests // The list of forecast data
>>>>>>> origin/master:app/src/main/java/computer/clay/protocolizer/MainActivityFragment.java
        );

        communication.listAdapter = listAdapter; // TODO: (HACK) This shouldn't be necessary or should be elsewhere!

        // Define the view (get a reference to it and pass it an adapter)
        ListView listView = (ListView) rootView.findViewById(R.id.listview_http_requests);
        listView.setAdapter (listAdapter);

//        listAdapter.notifyDataSetChanged ();

        // Handle TextView and display string from your list
//        ToggleButton ioBtn = (ToggleButton) listView.findViewById(R.id.io_btn);
//        ioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                if (isChecked) {
////                    // The toggle is enabled
////                    Toast toast = Toast.makeText(getActivity(), (String) "set as output", Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
////                    toast.show();
////                } else {
////                    // The toggle is disabled
////                    Toast toast = Toast.makeText(getActivity(), (String) "set as input", Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
////                    toast.show();
////                }
//            }
//        });

//        ToggleButton lightBtn = (ToggleButton) listView.findViewById(R.id.light_btn);
//        lightBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                if (isChecked) {
////                    // The toggle is enabled
////                    Toast toast = Toast.makeText(getActivity(), (String) "light on", Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
////                    toast.show();
////                } else {
////                    // The toggle is disabled
////                    Toast toast = Toast.makeText(getActivity(), (String) "light off", Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
////                    toast.show();
////                }
//            }
//        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

//                Context context = view.getContext();
//                String httpRequestText = listAdapter.getItem(position); //CharSequence text = "Hello toast!";
////                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(getActivity(), httpRequestText, Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

<<<<<<< HEAD:app/src/main/java/computer/clay/sculptor/sculptor/MainActivityFragment.java
                if (httpRequestText.equals("create")) {

                    // Add a new behavior construct to the looping sequence.
                    behaviorSequence.add("<Construct>");
                    httpRequestAdapter.notifyDataSetChanged();

                }

                /*
                HttpRequestTask httpRequestTask = new HttpRequestTask();
//                httpRequestTask.execute("94110");
                httpRequestTask.execute(httpRequestAdapter.getItem(position));
                */
=======
                UdpDatagramTask udpDatagramTask = new UdpDatagramTask();
//                httpRequestTask.execute("94110");
                udpDatagramTask.execute (listAdapter.getItem (position));
>>>>>>> origin/master:app/src/main/java/computer/clay/protocolizer/MainActivityFragment.java

//                // Executed in an Activity, so 'this' is the Context
//                // The fileUrl is a string URL, such as "http://www.example.com/image.png"
//                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(detailIntent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

//                Context context = view.getContext();
                String httpRequestText = listAdapter.getItem(position); //CharSequence text = "Hello toast!";
//                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), httpRequestText, Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                HttpRequestTask httpRequestTask = new HttpRequestTask();
//                httpRequestTask.execute("94110");
                httpRequestTask.execute (listAdapter.getItem (position));


                /*
                Intent settingsIntent = new Intent(getActivity(), HttpRequestActivity.class);
                startActivity(settingsIntent);
                */

                return false;
            }
        });

        // Disable the scrollbars.
        listView.setScrollbarFadingEnabled(false);
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);

        // Disable overscroll effect.
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // TODO: Handle the selected options item.

        return super.onOptionsItemSelected(item);
    }

    public class UdpDatagramTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
                /* Get weather data from an Internet source. */

            if (params.length == 0) {
                return null;
            }

            communication.sendDatagram(params[0], "connected");
//            communication.sendDatagram (params[0]);

            // This only happens if there was an error getting or parsing the forecast.
            return null;
        }
    }

    public class HttpRequestTask extends AsyncTask<String, Void, String[]> { // Extend AsyncTask and use void generics (for now)

        private final String LOG_TAG = HttpRequestTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
                /* Get weather data from an Internet source. */

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String[] forecastData;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

//                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

//                Uri.Builder builder = new Uri.Builder ();
//                builder.scheme("http")
//                        .authority("api.openweathermap.org")
//                        .appendPath("data")
//                        .appendPath("2.5")
//                        .appendPath("forecast")
//                        .appendPath("daily")
//                        .appendQueryParameter("q", postcodes[0])
//                        .appendQueryParameter("mode", "json")
//                        .appendQueryParameter("units", "metric")
//                        .appendQueryParameter("cnt", "7");
//
//                URL url = new URL (builder.build().toString());

                //final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String CLAY_UNIT_BASE_URL = "http://192.168.43.235/message?";
                final String CONTENT_PARAM = "content";
//                final String FORMAT_PARAM = "mode";
//                final String UNITS_PARAM = "units";
//                final String DAYS_PARAM = "cnt";

                // This approach enables the user to set the zip code from the settings activity.
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()); // Get preferences for this activity
                String remoteHostUri = prefs.getString(getString(R.string.pref_remote_host_key), // If there's a value stored for the location key in preferences, use it...
                        getString(R.string.pref_remote_host_default));

                // TODO: Replace the " " space character with "%20" string.

                //Uri builtUri = Uri.parse(remoteHostUri).buildUpon()
                Uri builtUri = Uri.parse(CLAY_UNIT_BASE_URL).buildUpon()
                        .appendQueryParameter(CONTENT_PARAM, params[0])
//                        .appendQueryParameter(FORMAT_PARAM, format)
//                        .appendQueryParameter(UNITS_PARAM, units)
//                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                URL url = new URL(builtUri.toString());




                communication.sendDatagram (params[0]);

//                // Broadcast UDP packet to the specified address.
//                String messageStr = params[0]; // "turn light 1 on";
//                int local_port = 4445;
//                int server_port = 4445;
//                DatagramSocket s = new DatagramSocket(local_port);
////                InetAddress local = InetAddress.getByName("192.168.43.235");
//                InetAddress local = InetAddress.getByName("255.255.255.255");
//                int msg_length = messageStr.length();
//                byte[] message = messageStr.getBytes();
//                DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
//                s.send(p);
//                s.close();





                Log.v(LOG_TAG, "SENDING REQUEST TO: " + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            // All this just for: return getWeatherDataFromJson(forecastJsonStr, numDays);
            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This only happens if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                listAdapter.clear ();
                for (String dayForecastStr : result) {
                    listAdapter.add (dayForecastStr);
                }
                // New day is back from the server at this point!

                // NOTE: Array adapter internally calls: adapter.notifyDataSetChanged()
            }
        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            return resultStrs;

        }
    }
}