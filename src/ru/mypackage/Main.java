package ru.mypackage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    public static void main(String[] args) {
    final String ENDPOINT = "https://restcountries.eu/rest/v2/";
    final String METHOD_BYNAME = "name/";
    final String METHOD_BYCODE = "alpha/";
    final String TARGET_COUNTRY = "Poland";
    final int HTTP_STATUS_SUCCESS = 200;
    final int HTTP_STATUS_NOTFOUND = 404;

        try{
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT + METHOD_BYNAME + TARGET_COUNTRY.toLowerCase()))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == HTTP_STATUS_SUCCESS){
                Object obj = JSONValue.parseWithException(response.body());
                JSONArray jsonArrayCountries = (JSONArray)obj;

                if(jsonArrayCountries.size() > 0){
                    JSONObject jsonObjectCountry = (JSONObject)jsonArrayCountries.get(0);
                    JSONArray jsonArrayBorders = (JSONArray) jsonObjectCountry.get("borders");

                    StringBuilder stringBuilder = new StringBuilder();
                    for(var countryAlias : jsonArrayBorders){
                        stringBuilder.append((stringBuilder.toString().equals("")) ? countryAlias: ";" + countryAlias);
                    }
                    String bordersStr = stringBuilder.toString();

                    var uri = ENDPOINT + METHOD_BYCODE + "?codes=" + bordersStr;
                    httpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(uri))
                            .GET()
                            .build();

                    response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                    if(response.statusCode() == HTTP_STATUS_SUCCESS){
                        obj = JSONValue.parseWithException(response.body());

                        jsonArrayCountries = (JSONArray)obj;
                        if(jsonArrayCountries.size() > 0){
                            for(int i=0; i<jsonArrayCountries.size(); i++){
                                jsonObjectCountry = (JSONObject)jsonArrayCountries.get(i);

                                stringBuilder = new StringBuilder();
                                JSONArray jsonArrayLang = (JSONArray) jsonObjectCountry.get("languages");
                                JSONObject jsonObjectLang = null;
                                for(int ii=0; ii<jsonArrayLang.size(); ii++){
                                    jsonObjectLang = (JSONObject) jsonArrayLang.get(ii);
                                    stringBuilder.append((stringBuilder.toString().equals("")) ? jsonObjectLang.get("name") : ", " + jsonObjectLang.get("name"));
                                }

                                String lang = stringBuilder.toString();

                                stringBuilder = new StringBuilder();
                                stringBuilder.append(jsonObjectCountry.get("name") + ". ")
                                        .append("Capital " + jsonObjectCountry.get("capital") + ". ")
                                        .append((jsonArrayLang.size() > 1) ? "Languages " + lang + "." : "Language " + lang + ".");

                                //result
                                System.out.println(stringBuilder.toString());
                            }


                        }
                    }

                }
            }

        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
