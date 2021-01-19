package com.example.buysell2.common;

import android.content.Context;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buysell2.Activity.MainActivity;
import com.example.buysell2.CShowProgress;
import com.example.buysell2.Do.SupplierMasterDo;
import com.example.buysell2.Interface.GetData;
import com.example.buysell2.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiServices {
    public CShowProgress cShowProgress;

    public static String getDataForSingleUser(String Userid) {
        BufferedReader reader = null;
        String response = "";
        try {
            URL getUrl = new URL(ServiceURLs.GET_USER_PROFILE + Userid);
            HttpURLConnection urlConnection = (HttpURLConnection) getUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept", "application/json");

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response += line;
            }


        } catch (Exception e) {
            e.printStackTrace();
            response = e.getMessage();
            return null;
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public static String Validating(String url, String from) {
        BufferedReader reader = null;
        String response = "";
        int responseCode = 0;
        try {
            URL getUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) getUrl.openConnection();
            if (from.equalsIgnoreCase("update Password"))
                urlConnection.setRequestMethod("PUT");
            else
                urlConnection.setRequestMethod("GET");
            responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                response = "valid";
            } else if (responseCode == 404) {
                response = "Invalid";
            }
//            urlConnection.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
            response = e.getMessage();
            return null;
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public static String EncodeToBytes(String userName, String password) {
        byte[] loginBytes = (userName + ":" + password).getBytes();

        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));
        return loginBuilder.toString();
    }

    public static String PostDataForCreateUser(String jsonString) {
        OutputStream out = null;
        String response = "";
        BufferedWriter writer = null;
        int responseCode = 0;
        try {
            URL postUrl = new URL(ServiceURLs.Registration);
            HttpURLConnection urlConnection = (HttpURLConnection) postUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(jsonString.getBytes("UTF-8"));
            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                response = AppConstants.INTERNAL_ERROR;
            } else if (responseCode == AppConstants.EmailId_Already_Exists) {
                response = AppConstants.EMAIL_ALREADY_EXISTS;
            } else if (responseCode == AppConstants.MobileNo_Already_Exists) {
                response = AppConstants.MOBILE_NO_ALREADY_EXISTS;
            } else if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = AppConstants.CREATED_SUCCESSFULLY;
            }
            urlConnection.getInputStream();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = e.getMessage();
        }
        return response;
    }

    public static String PutDataForCreateUser(String jsonString, long id) {
        OutputStream out = null;
        String response = "";
        BufferedWriter writer = null;
        int responseCode = 0;
        try {
            URL postUrl = new URL(ServiceURLs.UPDATE_PROFILE + "" + id);
            HttpURLConnection urlConnection = (HttpURLConnection) postUrl.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(jsonString.getBytes("UTF-8"));
            urlConnection.getInputStream();

            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                response = AppConstants.INTERNAL_ERROR;
            } else if (responseCode == AppConstants.EmailId_Already_Exists) {
                response = AppConstants.EMAIL_ALREADY_EXISTS;
            } else if (responseCode == AppConstants.MobileNo_Already_Exists) {
                response = AppConstants.MOBILE_NO_ALREADY_EXISTS;
            } else if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = AppConstants.CREATED_SUCCESSFULLY;
            } else if (responseCode == HttpsURLConnection.HTTP_NO_CONTENT) {
                response = AppConstants.NO_CONTENT;
            } else if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND) {
                response = AppConstants.USER_NOT_FOUND;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return response;
    }

    public static String getLoginToken(String jsonString) {
        OutputStream out = null;
        String response = "";
        BufferedWriter writer = null;
        int responseCode = 0;
        BufferedReader reader = null;
        try {
            URL postUrl = new URL(ServiceURLs.LOGIN);
            HttpURLConnection urlConnection = (HttpURLConnection) postUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(jsonString.getBytes("UTF-8"));
            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                response = AppConstants.INVALID_USERID_PASS;
            } else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                response = AppConstants.INTERNAL_ERROR;
            }
            urlConnection.getInputStream();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = e.getMessage();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public static String PostDatausingToken(String jsonString, String Url, String token) {
        OutputStream out = null;
        String response = "";
        BufferedWriter writer = null;
        int responseCode = 0;
        BufferedReader reader = null;
        try {
            URL postUrl = new URL(Url);
            HttpURLConnection urlConnection = (HttpURLConnection) postUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(jsonString.getBytes("UTF-8"));
            responseCode = urlConnection.getResponseCode();
            if (responseCode == AppConstants.Supplier_Name_Already_Exists) {
                response = AppConstants.SUPPLIER_NAME_ALREADY_EXISTS;
            } else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                response = AppConstants.INTERNAL_ERROR;
            } else if (responseCode == AppConstants.PAN_No_Already_Exists) {
                response = AppConstants.PAN_NO_ALREADY_EXISTS;
            } else if (responseCode == AppConstants.GST_No_Already_Exists) {
                response = AppConstants.GST_NO_ALREADY_EXISTS;
            } else if (responseCode == AppConstants.Mobile_No_Already_Exists_Supplier) {
                response = AppConstants.MOBILE_NO_ALREADY_EXISTS;
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                response = AppConstants.BAD_REQEUST;
            } else if (responseCode == AppConstants.UserId_not_found) {
                response = AppConstants.USERID_NOT_FOUND;
            }
            urlConnection.getInputStream();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                String line;
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = e.getMessage();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
    public static String PostData(String jsonString, String Url, String token) {
        OutputStream out = null;
        String response = "";
        BufferedWriter writer = null;
        int responseCode = 0;
        BufferedReader reader = null;
        try {
            URL postUrl = new URL(Url);
            HttpURLConnection urlConnection = (HttpURLConnection) postUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(jsonString.getBytes("UTF-8"));
            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                response = AppConstants.INTERNAL_ERROR;
            }
            urlConnection.getInputStream();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = "success";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = e.getMessage();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
    public static String deleteItem(String url, String token) {
        OutputStream out = null;
        String response = "";
        BufferedWriter writer = null;
        int responseCode = 0;
        BufferedReader reader = null;
        try {
            URL postUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) postUrl.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                response = AppConstants.INTERNAL_ERROR;
            }
            urlConnection.getInputStream();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = "delete";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = e.getMessage();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public static String UpdateItemUsingToken(String url, String jsonString, String token) {
        OutputStream out = null;
        String response = "";
        BufferedWriter writer = null;
        int responseCode = 0;
        BufferedReader reader = null;
        try {
            URL postUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) postUrl.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(jsonString.getBytes("UTF-8"));
            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                response = AppConstants.INTERNAL_ERROR;
            }
            urlConnection.getInputStream();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = "Updated";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = e.getMessage();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public static String getSearchData(String url, String token) {
        OutputStream out = null;
        String response = "";
        BufferedWriter writer = null;
        int responseCode = 0;
        BufferedReader reader = null;
        try {
            URL postUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) postUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                response = AppConstants.INTERNAL_ERROR;
            }
            urlConnection.getInputStream();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = e.getMessage();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public static String postForFavSupplier(String url, String token) {
        OutputStream out = null;
        String response = "";
        BufferedWriter writer = null;
        int responseCode = 0;
        BufferedReader reader = null;
        try {
            URL postUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) postUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_SERVER_ERROR) {
                response = AppConstants.INTERNAL_ERROR;
            }
            urlConnection.getInputStream();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = "Success";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = e.getMessage();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

}
