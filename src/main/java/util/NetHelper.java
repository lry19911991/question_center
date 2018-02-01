package util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.apache.http.Consts.UTF_8;

/**
 * <p>Description: http工具类</p>
 *
 * @author leo
 * @version V1.0
 */
public class NetHelper {
    private static final Logger logger = LoggerFactory.getLogger(NetHelper.class);

    /**
     * 获取MAC地址
     *
     * @return
     */
    public static String getMacAddress() {
        StringBuffer macAddress = new StringBuffer();
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();
                if (null != mac && mac.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        macAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("获取MAC地址出错", ex);
        }
        if (macAddress.length() == 0) {
            macAddress.append(UUID.randomUUID().toString());
        }
        logger.debug("本机MacAddress：->" + macAddress);
        return macAddress.toString();
    }

    /**
     * 文件上传
     *
     * @param url
     * @param parameter
     * @param files
     * @return
     */
    public static String uploader(String url, Map<String, String> parameter, Map<String, File> files) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(url);
        try {
            httpClient = HttpClients.createDefault();
            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(60000)
                    .setConnectTimeout(60000)
                    .setSocketTimeout(60000).build();
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder = builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            for (Map.Entry<String, String> params : parameter.entrySet()) {
                builder = builder.addTextBody(params.getKey(), params.getValue(), ContentType.create("text/plain", UTF_8));
            }
            for (Map.Entry<String, File> params : files.entrySet()) {
                builder = builder.addBinaryBody(params.getKey(), params.getValue(), ContentType.MULTIPART_FORM_DATA, params.getValue().getName());
            }
            post.setEntity(builder.build());
            post.setConfig(config);

            response = httpClient.execute(post);
            // 打印响应状态
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity, Charset.forName("UTF-8"));
            // 销毁
            EntityUtils.consume(resEntity);
            return result;
        } catch (Exception ex) {
            logger.error("文件上传出错", ex);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }

            } catch (Exception ex) {
                logger.error("文件上传出错", ex);
            }
        }
        return null;
    }

    /**
     * 多文件上传
     *
     * @param url
     * @param tokens
     * @param filename
     * @param files
     * @return
     */
    public static String uploader(String url, List<String> tokens, List<String> filename, List<byte[]> files) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(url);
        try {
            httpClient = HttpClients.createDefault();
            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000).build();
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder = builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (tokens.size() != filename.size() || filename.size() != files.size()) {
//                throw LogicException.generateException("三个参数数量必须一致：tokens,names,files");
            }
            for (int i = 0; i < tokens.size(); i++) {
                builder = builder.addTextBody("tokens", tokens.get(i), ContentType.create("text/plain", UTF_8));
                builder = builder.addBinaryBody("files", files.get(i), ContentType.MULTIPART_FORM_DATA, filename.get(i));
            }
            post.setEntity(builder.build());
            post.setConfig(config);
            response = httpClient.execute(post);
            // 打印响应状态
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity, UTF_8);
            // 销毁
            EntityUtils.consume(resEntity);
            return result;
        } catch (Exception ex) {
            logger.error("文件上传出错", ex);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (null != response) {
                    response.close();
                }
            } catch (Exception ex) {
                logger.error("文件上传出错", ex);
            }
        }

        return null;
    }

    /**
     * @param url
     * @param token
     * @param filename
     * @param file
     * @return
     */
    public static String uploader(String url, String token, String filename, byte[] file) {
        return uploader(url, Arrays.asList(token), Arrays.asList(filename), Arrays.asList(file));
    }


    /**
     * 创建一个SSL信任所有证书的httpClient对象
     *
     * @return
     */
    public static CloseableHttpClient createSSLInsecureClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 默认信任所有证书
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setSSLSocketFactory(sslcsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }


    /**
     * @param url       url 地址
     * @param parameter body 参数
     * @param hashMap   请求头
     * @return
     */
    public static String getHttps(String url, HashMap<String, String> hashMap) {
        CloseableHttpClient httpClient = createSSLInsecureClient();
        CloseableHttpResponse response = null;
        HttpGet get = new HttpGet(url);
        try {
            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000).build();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            for (Map.Entry<String, String> entry : parameter.entrySet()) {
//                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//            }
//            UrlEncodedFormEntity ent/ity = new UrlEncodedFormEntity(params, UTF_8);

//            StringEntity httpEntity=new StringEntity(parameter);


//            post.setEntity();
            get.setHeader("Content-Type", "application/json;charset=UTF-8");

            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                get.setHeader(entry.getKey(), entry.getValue());

            }
//            post.setHeader("Content-Type",);
            response = httpClient.execute(get);
            // 打印响应状态
            System.out.println(response.getStatusLine().getStatusCode());
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity, UTF_8);
            // 销毁
            EntityUtils.consume(resEntity);
            return result;
        } catch (Exception ex) {
            logger.error("post出错", ex);
        } finally {
            try {
                httpClient.close();
                if (null != response) {
                    response.close();
                }
            } catch (Exception ex) {
                logger.error("post出错", ex);
            }
        }
        return null;
    }


    /**
     * @param url       url 地址
     * @param parameter body 参数
     * @param hashMap   请求头
     * @return
     */
    public static String getHttp(String url, HashMap<String, String> hashMap) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet get = new HttpGet(url);
        try {
            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000).build();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            for (Map.Entry<String, String> entry : parameter.entrySet()) {
//                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//            }
//            UrlEncodedFormEntity ent/ity = new UrlEncodedFormEntity(params, UTF_8);

//            StringEntity httpEntity=new StringEntity(parameter);

            get.setConfig(config);
            get.setHeader("Content-Type", "application/json;charset=UTF-8");

            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                get.setHeader(entry.getKey(), entry.getValue());

            }
//            post.setHeader("Content-Type",);
            response = httpClient.execute(get);
            // 打印响应状态
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity, UTF_8);
            // 销毁
            EntityUtils.consume(resEntity);
            return result;
        } catch (Exception ex) {
            logger.error("post出错", ex);
        } finally {
            try {
                httpClient.close();
                if (null != response) {
                    response.close();
                }
            } catch (Exception ex) {
                logger.error("post出错", ex);
            }
        }
        return null;
    }


    /**
     * @param url       url 地址
     * @param parameter body 参数
     * @param hashMap   请求头
     * @return
     */
    public static String postHttps(String url, String parameter, HashMap<String, String> hashMap) {
        CloseableHttpClient httpClient = createSSLInsecureClient();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(url);
        try {
            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000).build();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            for (Map.Entry<String, String> entry : parameter.entrySet()) {
//                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//            }
//            UrlEncodedFormEntity ent/ity = new UrlEncodedFormEntity(params, UTF_8);

//            StringEntity httpEntity=new StringEntity(parameter);

            if(!StringUtils.isEmpty(parameter)){
                StringEntity content = new StringEntity(parameter, Charset.forName("utf-8"));// 第二个参数，设置后才会对，内容进行编码
                content.setContentType("application/json; charset=UTF-8");
                content.setContentEncoding("utf-8");
                post.setEntity(content);
            }

            post.setConfig(config);
            post.setHeader("Content-Type", "application/json;charset=UTF-8");

            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                post.setHeader(entry.getKey(), entry.getValue());

            }
//            post.setHeader("Content-Type",);
            response = httpClient.execute(post);
            // 打印响应状态
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity, UTF_8);
            // 销毁
            EntityUtils.consume(resEntity);
            return result;
        } catch (Exception ex) {
            logger.error("post出错", ex);
        } finally {
            try {
                httpClient.close();
                if (null != response) {
                    response.close();
                }
            } catch (Exception ex) {
                logger.error("post出错", ex);
            }
        }
        return null;
    }



    public static String post(String url, String parameter) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(url);
        try {
            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000).build();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            for (Map.Entry<String, String> entry : parameter.entrySet()) {
//                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//            }
//            UrlEncodedFormEntity ent/ity = new UrlEncodedFormEntity(params, UTF_8);
            StringEntity httpEntity = new StringEntity(parameter);
//            post.setEntity();
            post.setEntity(httpEntity);
            post.setConfig(config);
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
//            post.setHeader("Content-Type",);
            response = httpClient.execute(post);
            // 打印响应状态
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity, UTF_8);
            // 销毁
            EntityUtils.consume(resEntity);
            return result;
        } catch (Exception ex) {
            logger.error("post出错", ex);
        } finally {
            try {
                httpClient.close();
                if (null != response) {
                    response.close();
                }
            } catch (Exception ex) {
                logger.error("post出错", ex);
            }
        }
        return null;
    }

    public static String get(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet get = new HttpGet(url);
        try {
            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000).build();
            get.setConfig(config);
            response = httpClient.execute(get);
            // 打印响应状态
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity, UTF_8);
            // 销毁
            EntityUtils.consume(resEntity);
            return result;
        } catch (Exception ex) {
            logger.error("get出错", ex);
        } finally {
            try {
                httpClient.close();
                if (null != response) {
                    response.close();
                }
            } catch (Exception ex) {
                logger.error("get出错", ex);
            }
        }
        return null;
    }

}
