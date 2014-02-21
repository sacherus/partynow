package shit;
//package com.example.invidualproject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.ProgressDialog;
//import android.os.AsyncTask;
//
//class AsyncListViewLoader extends AsyncTask<String, Void, String> {
//    private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
//     
//    @Override
//    protected void onPostExecute(String p) {           
//        super.onPostExecute(result);
//        dialog.dismiss();
//        adpt.setItemList(result);
//        adpt.notifyDataSetChanged();
//    }
// 
//    @Override
//    protected void onPreExecute() {       
//        super.onPreExecute();
//        dialog.setMessage("Downloading contacts...");
//        dialog.show();           
//    }
// 
//    @Override
//    protected String doInBackground(String... params) {
//         
//        try {
//            URL u = new URL(params[0]);
//             
//            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//            conn.setRequestMethod("GET");
//             
//            conn.connect();
//            InputStream is = conn.getInputStream();
//             
//            // Read the stream
//            byte[] b = new byte[1024];
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//             
//            while ( is.read(b) != -1)
//                baos.write(b);
//             
//            String JSONResp = new String(baos.toByteArray());
//            JSONArray arr = new JSONArray(JSONResp);
//             
//            return result;
//        }
//        catch(Throwable t) {
//            t.printStackTrace();
//        }
//        return null;
//    }
//     
//
//     
//}
