package com.oerdev.traveller.PlaceDetails.dump;

import android.support.v7.app.AppCompatActivity;

public class DumpDetailedActivity extends AppCompatActivity {

//    private static final String TAG = DumpDetailedActivity.class.getSimpleName();
//    private static final String GET_SN = "sn";
//    private static final String SEND_LIKE = "like";
//    private static final String SEND_COMMENT = "comment";
//    private static final String ADD_MYPLACE = "myplace";
//    public static Context mContext;
//    TextView titleText;
//    TextView snText;
//    TextView detailText;
//    ImageView titleImage;
//    ImageView bookmarkImage;
//    LikesRecyclerViewAdapter mLikesAdapter;
//    CommentsRecyclerViewAdapter mCommentsAdapter;
//    private String placeId;
//    private String fromFragment;
//    private JSONArray jsonLikes;
//    private JSONArray jsonComments;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.detailed_content_views);
//        mContext = getApplicationContext();
//
//
//        Bundle bundle = getIntent().getExtras();
//        placeId = bundle.getString(AppConfigs.INTENT_EXTRA_PLACE_ID);
//        fromFragment = bundle.getString(AppConfigs.INTENT_EXTRA_FROM_FRAGMENT);
//        int tappedItem = bundle.getInt(AppConfigs.INTENT_EXTRA_TAPPED_ITEM);
//
//        if (placeId != null) {
//            new snAsync().execute(GET_SN);
//        }
//
//        Logger.d(TAG, "placeId: " + placeId + ", fromFragment: " + fromFragment + ", tappedItem: " + tappedItem);
//
////        titleText = (TextView) findViewById(R.id.title_text);
////        snText = (TextView) findViewById(R.id.sn);
////        detailText = (TextView) findViewById(R.id.place_detail);
//
//        if (fromFragment.equals(AppConfigs.FRAGMENT_HANGOUT)) {
//            Glide.with(this).load(HangoutFragment.HANGOUT_ITEM_MAP.get(placeId).primaryImageUrl).into(titleImage);
//            titleText.setText(HangoutFragment.HANGOUT_ITEM_MAP.get(placeId).name);
//            snText.setText(HangoutFragment.HANGOUT_ITEM_MAP.get(placeId).likes + " Likes   |   "
//                    + HangoutFragment.HANGOUT_ITEM_MAP.get(placeId).comments + " Comments");
//            detailText.setText(HangoutFragment.HANGOUT_ITEM_MAP.get(placeId).details);
//        }
//
//        if (fromFragment.equals(AppConfigs.FRAGMENT_SUGGEST)) {
//            Glide.with(this).load(SuggestFragment.SUGGEST_ITEM_MAP.get(placeId).primaryImageUrl).into(titleImage);
//            titleText.setText(SuggestFragment.SUGGEST_ITEM_MAP.get(placeId).name);
//            snText.setText(SuggestFragment.SUGGEST_ITEM_MAP.get(placeId).likes + " Likes   |   "
//                    + SuggestFragment.SUGGEST_ITEM_MAP.get(placeId).comments + " Comments");
//            detailText.setText(SuggestFragment.SUGGEST_ITEM_MAP.get(placeId).details);
//        }
//
//        jsonLikes = new JSONArray();
//        jsonComments = new JSONArray();
//
//        bookmarkImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new snAsync().execute(ADD_MYPLACE, AppConfigs.ADD);
//            }
//        });
//        ((Button) findViewById(R.id.comment_btn)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String comment = ((EditText) findViewById(R.id.comment_here)).getText().toString();
//                new snAsync().execute(SEND_COMMENT, comment);
//            }
//        });
//
//        ((ImageView) findViewById(R.id.like_img)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new snAsync().execute(SEND_LIKE);
//            }
//        });
//    }
//
//    class snAsync extends AsyncTask<String, Void, String> {
//
//        public static final int CONNECTION_TIMEOUT = 10000;
//        public static final int READ_TIMEOUT = 15000;
//        HttpURLConnection conn;
//        URL url = null;
//        String requestType;
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                Logger.d(TAG, "Connecting to server. Expect some log of status");
//                String query = null;
//                Uri.Builder builder = null;
//                requestType = params[0];
//
//                switch (params[0]) {
//
//                    case GET_SN:
//                        url = new URL(AppConfigs.URL_GET_SN_ALL +
//                                SessionManager.getInstance(mContext).getUserId() +
//                                "/" + placeId);
//                        break;
//                    case SEND_COMMENT:
//                        url = new URL(AppConfigs.URL_COMMENT_PLACE +
//                                SessionManager.getInstance(mContext).getUserId() +
//                                "/" + placeId);
//                        builder = new Uri.Builder()
//                                .appendQueryParameter(AppConfigs.EXTRA_COMMENT, params[1]);
//                        query = builder.build().getEncodedQuery();
//                        break;
//                    case SEND_LIKE:
//                        url = new URL(AppConfigs.URL_LIKE_PLACE +
//                                SessionManager.getInstance(mContext).getUserId() +
//                                "/" + placeId);
//                        break;
//                    case ADD_MYPLACE:
//                        url = new URL(AppConfigs.URL_UPDATE_MYPLAN +
//                                SessionManager.getInstance(mContext).getUserId() +
//                                "/" + placeId);
//                        builder = new Uri.Builder()
//                                .appendQueryParameter(AppConfigs.EXTRA_DO_ACTION, params[1]);
//                        query = builder.build().getEncodedQuery();
//                        break;
//                }
//
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(READ_TIMEOUT);
//                conn.setConnectTimeout(CONNECTION_TIMEOUT);
//                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                // Open connection for sending data
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                if (query != null) {
//                    writer.write(query);
//                }
//                writer.flush();
//                writer.close();
//                os.close();
//                conn.connect();
//
//                // check response of initiated connection
//                int response_code = conn.getResponseCode();
//
//                Logger.d(TAG, "Response code: " + response_code);
//                if (response_code == HttpURLConnection.HTTP_OK) {
//                    // Read data sent from server
//                    InputStream input = conn.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    StringBuilder result = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        result.append(line);
//                    }
//                    Logger.d(TAG, "server resp : " + result.toString());
//
//                    JSONObject jobj = new JSONObject(result.toString());
//                    if (params[0].equals(GET_SN) || params[0].equals(SEND_LIKE)) {
//                        jsonLikes = jobj.getJSONArray("likes");
//                    }
//                    if (params[0].equals(GET_SN) || params[0].equals(SEND_COMMENT)) {
//                        jsonComments = jobj.getJSONArray("comments");
//                    }
//                    return result.toString();
//                } else {
//                    return Integer.toString(response_code);
//                }
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                return "error";
//            } catch (IOException e) {
//                e.printStackTrace();
//                Logger.d(TAG, "Reason:" + e.getCause());
//                return "error";
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return "error";
//        }
//
//        @Override
//        protected void onPostExecute(String response) {
//            Logger.d(TAG, "onPostExecute");
//
//            if (requestType.equals(SEND_LIKE) || requestType.equals(GET_SN)) {
//                RecyclerView lrecyclerView = (RecyclerView) findViewById(R.id.likes_recycler_view);
//                mLikesAdapter = new LikesRecyclerViewAdapter(jsonLikes);
//                lrecyclerView.setHasFixedSize(true);
//                RecyclerView.LayoutManager lLayoutManager =
//                        new LinearLayoutManager(getApplicationContext(),
//                                LinearLayoutManager.HORIZONTAL, true);
//                lLayoutManager.scrollToPosition(jsonLikes.length() - 1);
//                lrecyclerView.setLayoutManager(lLayoutManager);
//                lrecyclerView.setItemAnimator(new DefaultItemAnimator());
//                lrecyclerView.setAdapter(mLikesAdapter);
//                mLikesAdapter.notifyDataSetChanged();
//            }
//            if (requestType.equals(SEND_COMMENT) || requestType.equals(GET_SN)) {
//                RecyclerView crecyclerView = (RecyclerView) findViewById(R.id.comments_recycler_view);
//
//                mCommentsAdapter = new CommentsRecyclerViewAdapter(jsonComments);
//                crecyclerView.setHasFixedSize(true);
//                RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(getApplicationContext());
//                crecyclerView.setLayoutManager(cLayoutManager);
//                crecyclerView.setItemAnimator(new DefaultItemAnimator());
//                crecyclerView.setAdapter(mCommentsAdapter);
//                mCommentsAdapter.notifyDataSetChanged();
//            }
//
//            if (requestType.equals(SEND_COMMENT)) {
//                EditText et = ((EditText) findViewById(R.id.comment_here));
//                et.setText("");
//                ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(et.getWindowToken(), 0);
//            }
//
//            if (requestType.equals(ADD_MYPLACE)) {
//                HttpHandler httpHandler = new HttpHandler();
//                httpHandler.getMyPlan();
//            }
//
//            if (jsonLikes != null && jsonComments != null) {
//                snText.setText(jsonLikes.length() + " Likes   |   "
//                        + jsonComments.length() + " Comments");
//            }
//        }
//    }
}
