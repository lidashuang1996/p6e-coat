package club.p6e.coat.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Http Util
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class HttpUtil {

    /**
     * Definition
     */
    public interface Definition {

        /**
         * Do Get
         *
         * @param httpClient      Http Client Object
         * @param httpGet         Http Get Object
         * @param responseHandler Response Handler Object
         * @param <T>             Result Class Object
         * @return Result Object
         */
        <T> T doGet(HttpClient httpClient, HttpGet httpGet, ResponseHandler<T> responseHandler);

        /**
         * Do Get
         *
         * @param httpClient  Http Client Object
         * @param httpUrl     Http Url Object
         * @param httpHeaders Http Headers Object
         * @param httpParams  Http Params Object
         * @return Input Stream Object
         */
        InputStream doGet(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams);

        /**
         * Do Post
         *
         * @param httpClient      Http Client Object
         * @param httpPost        Http Post Object
         * @param responseHandler Response Handler Object
         * @param <T>             Result Class Object
         * @return Result Object
         */
        <T> T doPost(HttpClient httpClient, HttpPost httpPost, ResponseHandler<T> responseHandler);

        /**
         * Do Post
         *
         * @param httpClient  Http Client Object
         * @param httpUrl     Http Url Object
         * @param httpHeaders Http Headers Object
         * @param httpEntity  Http Entity Object
         * @return Input Stream Object
         */
        InputStream doPost(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, HttpEntity httpEntity);

        /**
         * Do Delete
         *
         * @param httpClient      Http Client Object
         * @param httpDelete      Http Delete Object
         * @param responseHandler Response Handler Object
         * @param <T>             Result Class Object
         * @return Result Object
         */
        <T> T doDelete(HttpClient httpClient, HttpDelete httpDelete, ResponseHandler<T> responseHandler);

        /**
         * Do Delete
         *
         * @param httpClient  Http Client Object
         * @param httpUrl     Http Url Object
         * @param httpHeaders Http Headers Object
         * @param httpParams  Http Params Object
         * @return Input Stream Object
         */
        InputStream doDelete(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams);

        /**
         * Do Put
         *
         * @param httpClient      Http Client Object
         * @param httpPut         Http Put Object
         * @param responseHandler Response Handler Object
         * @param <T>             Result Class Object
         * @return Result Object
         */
        <T> T doPut(HttpClient httpClient, HttpPut httpPut, ResponseHandler<T> responseHandler);

        /**
         * Do Put
         *
         * @param httpClient  Http Client Object
         * @param httpUrl     Http Url Object
         * @param httpHeaders Http Headers Object
         * @param httpEntity  Http Entity Object
         * @return Input Stream Object
         */
        InputStream doPut(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, HttpEntity httpEntity);

        /**
         * Do Network
         *
         * @param httpClient      Http Client Object
         * @param httpUriRequest  Http Uri Request Object
         * @param responseHandler Response Handler Object
         * @param <T>             Result Class Object
         * @return Result Object
         */
        <T> T doNetwork(HttpClient httpClient, HttpUriRequest httpUriRequest, ResponseHandler<T> responseHandler);

    }

    /**
     * Implementation
     */
    private static class Implementation implements Definition {

        /**
         * Inject Log Object
         */
        private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

        @Override
        public <T> T doGet(HttpClient httpClient, HttpGet httpGet, ResponseHandler<T> responseHandler) {
            return doNetwork(httpClient, httpGet, responseHandler);
        }

        @Override
        public InputStream doGet(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
            try {
                final HttpGet httpGet = new HttpGet();
                if (httpHeaders != null) {
                    httpHeaders.forEach(httpGet::setHeader);
                }
                httpGet.setURI(URI.create(WebUtil.mergeUrlParams(httpUrl, httpParams)));
                return doGet(httpClient, httpGet, (httpResponse) -> {
                    try {
                        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            if (httpResponse.getEntity() != null && httpResponse.getEntity().getContent() != null) {
                                return httpResponse.getEntity().getContent();
                            } else {
                                return null;
                            }
                        } else {
                            LOGGER.error("[ HTTP UTIL ] <GET> HTTP CODE NOT OK");
                            return null;
                        }
                    } catch (Exception e) {
                        LOGGER.error("[ HTTP UTIL ] <GET> RESULT ERROR >>> ", e);
                        return null;
                    }
                });
            } catch (Exception e) {
                LOGGER.error("[ HTTP UTIL ] <GET> ERROR >>> ", e);
                return null;
            }
        }

        @Override
        public <T> T doPost(HttpClient httpClient, HttpPost httpPost, ResponseHandler<T> responseHandler) {
            return doNetwork(httpClient, httpPost, responseHandler);
        }

        @Override
        public InputStream doPost(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, HttpEntity httpEntity) {
            try {
                final HttpPost httpPost = new HttpPost();
                if (httpHeaders != null) {
                    httpHeaders.forEach(httpPost::setHeader);
                }
                if (httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                httpPost.setURI(URI.create(httpUrl));
                return doPost(httpClient, httpPost, (httpResponse) -> {
                    try {
                        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            if (httpResponse.getEntity() != null && httpResponse.getEntity().getContent() != null) {
                                return httpResponse.getEntity().getContent();
                            } else {
                                return null;
                            }
                        } else {
                            LOGGER.error("[ HTTP UTIL ] <POST> HTTP CODE NOT OK");
                            return null;
                        }
                    } catch (Exception e) {
                        LOGGER.error("[ HTTP UTIL ] <POST> RESULT ERROR >>> ", e);
                        return null;
                    }
                });
            } catch (Exception e) {
                LOGGER.error("[ HTTP UTIL ] <POST> ERROR >>> ", e);
                return null;
            }
        }

        @Override
        public <T> T doDelete(HttpClient httpClient, HttpDelete httpDelete, ResponseHandler<T> responseHandler) {
            return doNetwork(httpClient, httpDelete, responseHandler);
        }

        @Override
        public InputStream doDelete(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
            try {
                final HttpDelete httpDelete = new HttpDelete();
                if (httpHeaders != null) {
                    httpHeaders.forEach(httpDelete::setHeader);
                }
                httpDelete.setURI(URI.create(WebUtil.mergeUrlParams(httpUrl, httpParams)));
                return doDelete(httpClient, httpDelete, (httpResponse) -> {
                    try {
                        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            if (httpResponse.getEntity() != null && httpResponse.getEntity().getContent() != null) {
                                return httpResponse.getEntity().getContent();
                            } else {
                                return null;
                            }
                        } else {
                            LOGGER.error("[ HTTP UTIL ] <DELETE> HTTP CODE NOT OK");
                            return null;
                        }
                    } catch (Exception e) {
                        LOGGER.error("[ HTTP UTIL ] <DELETE> RESULT ERROR >>> ", e);
                        return null;
                    }
                });
            } catch (Exception e) {
                LOGGER.error("[ HTTP UTIL ] <DELETE> ERROR >>> ", e);
                return null;
            }
        }

        @Override
        public <T> T doPut(HttpClient httpClient, HttpPut httpPut, ResponseHandler<T> responseHandler) {
            return doNetwork(httpClient, httpPut, responseHandler);
        }

        @Override
        public InputStream doPut(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, HttpEntity httpEntity) {
            try {
                final HttpPut httpPut = new HttpPut();
                if (httpHeaders != null) {
                    httpHeaders.forEach(httpPut::setHeader);
                }
                if (httpEntity != null) {
                    httpPut.setEntity(httpEntity);
                }
                httpPut.setURI(URI.create(httpUrl));
                return doPut(httpClient, httpPut, (httpResponse) -> {
                    try {
                        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            if (httpResponse.getEntity() != null && httpResponse.getEntity().getContent() != null) {
                                return httpResponse.getEntity().getContent();
                            } else {
                                return null;
                            }
                        } else {
                            LOGGER.error("[ HTTP UTIL ] <PUT> HTTP CODE NOT OK");
                            return null;
                        }
                    } catch (Exception e) {
                        LOGGER.error("[ HTTP UTIL ] <PUT> RESULT ERROR >>> ", e);
                        return null;
                    }
                });
            } catch (Exception e) {
                LOGGER.error("[ HTTP UTIL ] <PUT> ERROR >>> ", e);
                return null;
            }
        }

        @Override
        public <T> T doNetwork(HttpClient httpClient, HttpUriRequest httpUriRequest, ResponseHandler<T> responseHandler) {
            try {
                return httpClient.execute(httpUriRequest, responseHandler);
            } catch (Exception e) {
                LOGGER.error("[ HTTP UTIL ] <NETWORK> ERROR >>> ", e);
                return null;
            }
        }

    }

    /**
     * Http Client Object
     */
    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    /**
     * Default Definition Implementation Object
     */
    private static Definition DEFINITION = new Implementation();

    /**
     * Set Definition Implementation Object
     *
     * @param implementation Definition Implementation Object
     */
    public static void set(Definition implementation) {
        DEFINITION = implementation;
    }

    /**
     * Do Get
     *
     * @param httpUrl Http Url Object
     * @return Result String
     */
    public static String doGet(String httpUrl) {
        return doGet(httpUrl, null);
    }

    /**
     * Do Get
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @return Result String
     */
    public static String doGet(String httpUrl, Map<String, String> httpHeaders) {
        return doGet(httpUrl, httpHeaders, null);
    }

    /**
     * Do Get
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpParams  Http Params Object
     * @return Result String
     */
    public static String doGet(String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
        return doGet(HTTP_CLIENT, httpUrl, httpHeaders, httpParams);
    }

    /**
     * Do Get
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpParams  Http Params Object
     * @return Result String
     */
    public static String doGet(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
        return resultToString(DEFINITION.doGet(httpClient, httpUrl, httpHeaders, httpParams));
    }

    /**
     * Do Get
     *
     * @param httpClient      Http Client Object
     * @param httpGet         Http Get Object
     * @param responseHandler Response Handler Object
     * @param <T>             Result Class Object
     * @return Result Object
     */
    public static <T> T doGet(HttpClient httpClient, HttpGet httpGet, ResponseHandler<T> responseHandler) {
        return DEFINITION.doGet(httpClient, httpGet, responseHandler);
    }

    /**
     * Do Get Input Stream
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpParams  Http Params Object
     * @return Input Stream Object
     */
    public static InputStream doGetInputStream(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
        return DEFINITION.doGet(httpClient, httpUrl, httpHeaders, httpParams);
    }

    /**
     * Do Post
     *
     * @param httpUrl Http Url Object
     * @return Result String
     */
    public static String doPost(String httpUrl) {
        return doPost(httpUrl, null);
    }

    /**
     * Do Post
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @return Result String
     */
    public static String doPost(String httpUrl, Map<String, String> httpHeaders) {
        return doPost(httpUrl, httpHeaders, "");
    }

    /**
     * Do Post
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param body        Http Params Object
     * @return Result String
     */
    public static String doPost(String httpUrl, Map<String, String> httpHeaders, String body) {
        return doPost(HTTP_CLIENT, httpUrl, httpHeaders, body);
    }

    /**
     * Do Post
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpParams  Http Params Object
     * @return Result String
     */
    public static String doPost(String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
        return doPost(HTTP_CLIENT, httpUrl, httpHeaders, httpParams);
    }

    /**
     * Do Post
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpEntity  Http Entity Object
     * @return Result String
     */
    public static String doPost(String httpUrl, Map<String, String> httpHeaders, HttpEntity httpEntity) {
        return doPost(HTTP_CLIENT, httpUrl, httpHeaders, httpEntity);
    }

    /**
     * Do Post
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param body        Body String
     * @return Result String
     */
    public static String doPost(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, String body) {
        if (httpHeaders == null) {
            httpHeaders = new HashMap<>();
            httpHeaders.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        } else {
            httpHeaders.putIfAbsent(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        }
        return doPost(httpClient, httpUrl, httpHeaders, new StringEntity(body, StandardCharsets.UTF_8));
    }

    /**
     * Do Post
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpParams  Http Params Object
     * @return Result String
     */
    @SuppressWarnings("ALL")
    public static String doPost(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
        try {
            if (httpHeaders == null) {
                httpHeaders = new HashMap<>();
                httpHeaders.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            } else {
                httpHeaders.putIfAbsent(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            }
            final List<BasicNameValuePair> list = new ArrayList<>();
            if (httpParams != null) {
                list.addAll(httpParams.entrySet().stream().map(entry ->
                        new BasicNameValuePair(entry.getKey(), entry.getValue())).toList());
            }
            return doPost(httpClient, httpUrl, httpHeaders, new UrlEncodedFormEntity(list));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Do Post
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpEntity  Http Entity Object
     * @return Result String
     */
    public static String doPost(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, HttpEntity httpEntity) {
        return resultToString(DEFINITION.doPost(httpClient, httpUrl, httpHeaders, httpEntity));
    }

    /**
     * Do Post
     *
     * @param httpClient      Http Client Object
     * @param httpPost        Http Post Object
     * @param responseHandler Response Handler Object
     * @param <T>             Result Class Object
     * @return Result Object
     */
    public static <T> T doPost(HttpClient httpClient, HttpPost httpPost, ResponseHandler<T> responseHandler) {
        return DEFINITION.doPost(httpClient, httpPost, responseHandler);
    }

    /**
     * Do Post Input Stream
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpEntity  Http Entity Object
     * @return Input Stream Object
     */
    public static InputStream doPostInputStream(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, HttpEntity httpEntity) {
        return DEFINITION.doPost(httpClient, httpUrl, httpHeaders, httpEntity);
    }

    /**
     * Do Delete
     *
     * @param httpUrl Http Url Object
     * @return Result String
     */
    public static String doDelete(String httpUrl) {
        return doDelete(httpUrl, null);
    }

    /**
     * Do Delete
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @return Result String
     */
    public static String doDelete(String httpUrl, Map<String, String> httpHeaders) {
        return doDelete(httpUrl, httpHeaders, null);
    }

    /**
     * Do Delete
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpParams  Http Params Object
     * @return Result String
     */
    public static String doDelete(String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
        return doDelete(HTTP_CLIENT, httpUrl, httpHeaders, httpParams);
    }

    /**
     * Do Delete
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpParams  Http Params Object
     * @return Result String
     */
    public static String doDelete(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
        return resultToString(DEFINITION.doDelete(httpClient, httpUrl, httpHeaders, httpParams));
    }

    /**
     * Do Delete
     *
     * @param httpClient      Http Client Object
     * @param httpDelete      Http Delete Object
     * @param responseHandler Response Handler Object
     * @param <T>             Result Class Object
     * @return Result Object
     */
    public static <T> T doDelete(HttpClient httpClient, HttpDelete httpDelete, ResponseHandler<T> responseHandler) {
        return DEFINITION.doDelete(httpClient, httpDelete, responseHandler);
    }

    /**
     * Do Delete Input Stream
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpParams  Http Params Object
     * @return Input Stream Object
     */
    public static InputStream doDeleteInputStream(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
        return DEFINITION.doDelete(httpClient, httpUrl, httpHeaders, httpParams);
    }

    /**
     * Do Put
     *
     * @param httpUrl Http Url Object
     * @return Result String
     */
    public static String doPut(String httpUrl) {
        return doPut(httpUrl, null);
    }

    /**
     * Do Put
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @return Result String
     */
    public static String doPut(String httpUrl, Map<String, String> httpHeaders) {
        return doPut(httpUrl, httpHeaders, "");
    }

    /**
     * Do Put
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param body        Http Params Object
     * @return Result String
     */
    public static String doPut(String httpUrl, Map<String, String> httpHeaders, String body) {
        return doPut(HTTP_CLIENT, httpUrl, httpHeaders, body);
    }

    /**
     * Do Put
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpParams  Http Params Object
     * @return Result String
     */
    public static String doPut(String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
        return doPut(HTTP_CLIENT, httpUrl, httpHeaders, httpParams);
    }

    /**
     * Do Put
     *
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpEntity  Http Entity Object
     * @return Result String
     */
    public static String doPut(String httpUrl, Map<String, String> httpHeaders, HttpEntity httpEntity) {
        return doPut(HTTP_CLIENT, httpUrl, httpHeaders, httpEntity);
    }

    /**
     * Do Put
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param body        Body String
     * @return Result String
     */
    public static String doPut(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, String body) {
        if (httpHeaders == null) {
            httpHeaders = new HashMap<>();
            httpHeaders.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        } else {
            httpHeaders.putIfAbsent(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        }
        return doPut(httpClient, httpUrl, httpHeaders, new StringEntity(body, StandardCharsets.UTF_8));
    }

    /**
     * Do Put
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpParams  Http Params Object
     * @return Result String
     */
    @SuppressWarnings("ALL")
    public static String doPut(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, Map<String, String> httpParams) {
        try {
            if (httpHeaders == null) {
                httpHeaders = new HashMap<>();
                httpHeaders.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            } else {
                httpHeaders.putIfAbsent(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            }
            final List<BasicNameValuePair> list = new ArrayList<>();
            if (httpParams != null) {
                list.addAll(httpParams.entrySet().stream().map(entry ->
                        new BasicNameValuePair(entry.getKey(), entry.getValue())).toList());
            }
            return doPut(httpClient, httpUrl, httpHeaders, new UrlEncodedFormEntity(list));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Do Put
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpEntity  Http Entity Object
     * @return Result String
     */
    public static String doPut(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, HttpEntity httpEntity) {
        return resultToString(DEFINITION.doPut(httpClient, httpUrl, httpHeaders, httpEntity));
    }

    /**
     * Do Put
     *
     * @param httpClient      Http Client Object
     * @param httpPut         Http Put Object
     * @param responseHandler Response Handler Object
     * @param <T>             Result Class Object
     * @return Result Object
     */
    public static <T> T doPut(HttpClient httpClient, HttpPut httpPut, ResponseHandler<T> responseHandler) {
        return DEFINITION.doPut(httpClient, httpPut, responseHandler);
    }

    /**
     * Do Put Input Stream
     *
     * @param httpClient  Http Client Object
     * @param httpUrl     Http Url Object
     * @param httpHeaders Http Headers Object
     * @param httpEntity  Http Entity Object
     * @return Input Stream Object
     */
    public static InputStream doPutInputStream(HttpClient httpClient, String httpUrl, Map<String, String> httpHeaders, HttpEntity httpEntity) {
        return DEFINITION.doPut(httpClient, httpUrl, httpHeaders, httpEntity);
    }

    /**
     * Do Network
     *
     * @param httpClient      Http Client Object
     * @param httpUriRequest  Http Uri Request Object
     * @param responseHandler Response Handler Object
     * @param <T>             Result Class Object
     * @return Result Object
     */
    public static <T> T doNetwork(HttpClient httpClient, HttpUriRequest httpUriRequest, ResponseHandler<T> responseHandler) {
        return DEFINITION.doNetwork(httpClient, httpUriRequest, responseHandler);
    }

    /**
     * Result To String
     *
     * @param inputStream Input Stream Object
     * @return Result String
     */
    private static String resultToString(InputStream inputStream) {
        final StringBuilder result = new StringBuilder();
        if (inputStream != null) {
            try (
                    InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            ) {
                int tmp;
                while ((tmp = reader.read()) != -1) {
                    result.append((char) tmp);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }

}
