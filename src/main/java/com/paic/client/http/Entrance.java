package com.paic.client.http;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


/**
 * ClassName: TestClass &lt;br/&gt; Function: TODO ADD FUNCTION. &lt;br/&gt; Reason: TODO ADD
 * REASON(可选). &lt;br/&gt; date: 2017年6月16日 上午11:16:53 &lt;br/&gt;
 * 
 * @author BAOLIUSHISAN494
 * @version
 * @since JDK 1.6
 */
public class Entrance
{

    private static final Logger logger = Logger.getLogger(Entrance.class);

    private static final String URL_MAP_PROP = "url_map.properties";

    public static final String SUFFIX = ".xml";
    

    public static void main(String[] args)
        throws IOException
    {
         final AtomicInteger poolNumber = new AtomicInteger(1);

        final Charset charset = Charset.forName("UTF-8");

        if (args == null || args.length != 3)
        {
            logger.error("please inut right command: java -jar XX.jar [url] [tranCode] [loop]");
            // System.exit(1);
        }
        // final String tranCode;
        // final String key;
        // int loop;
        final String tranCode = "8127";
        final String key = "cnbs";
        final int loop = 1;
        // final String tranCode = args[0];
        // final String key = args[1];
        // final int loop = Integer.parseInt(args[2]);

        final String requestUrl = getUrl(key);

        logger.debug("requestUrl" + requestUrl + ",tranCode: " + tranCode);

        // URL url = Entrance.class.getClassLoader().getResource(tranCode + SUFFIX);
        // 用之前的方法取不到classpath路径下的文件

        InputStream xmlInput = ClassLoader.getSystemResourceAsStream(tranCode + SUFFIX);
        final byte[] requestData = IOUtils.toByteArray(xmlInput);

        // System.out.println(url);
        // System.out.println(url.getPath());
        // // File file = FileUtils.toFile(url);
        // File file = new File(url.getPath());
        // final byte[] requestData = FileUtils.readFileToByteArray(file);
        System.out.println(requestData == null ? "NULL" : requestData.length);

//        ExecutorService executor = ThreadPoolExecutor.Executors.newFixedThreadPool(10);
        ThreadFactory threadFactory = new ThreadFactory()
        {
            
            @Override
            public Thread newThread(Runnable r)
            {
                String name = "client-" + poolNumber.getAndIncrement()+"-thread";
                Thread t = new Thread(r, name);
                return t;
            }
        };
        ExecutorService executor = new ThreadPoolExecutor(5, 10, 3000,
            TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(),threadFactory);
        
        
        for (int i = 0; i < loop; i++ )
        {

            executor.execute(new Runnable()
            {

                @Override
                public void run()
                {
                    HttpClient client = new HttpClient();
                    byte[] responseDate = null;
                    long start = System.nanoTime();

                    responseDate = client.request(requestData, requestUrl);
                    System.out.println("TIME:" + (System.nanoTime() - start));
                    System.out.println(new String(responseDate, charset));
                }
            });

        }

        executor.shutdown();
        try
        {
            Thread.sleep(100000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.exit(1);
            IOUtils.closeQuietly(xmlInput);
        }

    }

    private static String getUrl(String key)
        throws IOException
    {
        InputStream input = ClassLoader.getSystemResourceAsStream(URL_MAP_PROP);
        Properties prop = new Properties();
        prop.load(input);
        final String requestUrl = prop.getProperty(key);
        return requestUrl;
    }

}
