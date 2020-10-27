package com.stereo.study.http;

import com.stereo.study.util.Time;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by liuj-ai on 2020/10/22.
 */
public class CloudtTest {
    public static void main(String[] args) throws Exception {
        final AtomicLong counter = new AtomicLong(0);
        final int concurrency = 100;
        final ExecutorService executorService = Executors.newFixedThreadPool(concurrency);
        final Map<String, String> headers = new HashMap<>();
        headers.put("cookie", "__gm_lec=; .ENDPOINT=WEB; .CLOUDT_TENANT_ID=1438415; __gm_ici=90f31807bb014717b0672067d816f6a8; __gm.t=eyJhbGciOiJIUzUxMiJ9.WlhsS2FHSkhZMmxQYVVwcllWaEphVXhEU214aWJVMXBUMmxLUWsxVVNUUlJNRXBFVEZWb1ZFMXFWVEpKYmpBdUxsWmlhamw1VldwdGFtZFVNRFkzVmtGeFdHVjBaR2N1VmtKc1drbzFNSFpNVGpoV1YwRldlazFZVGxOWGEzZG9RbGcxUmxCbU56Z3pMWHBhV0VSVU5WTTJTMjlvTURRM1NHdFdUbmRxWDNCbWNsSkJWVTlLZW01R1pWbGFSa2hVTlc5MU5qZGpSVXBxWkRkWldtSnliSGxTWjBkemMwaE9USGhHYVVka2FHeENSbTA1VjB4UFkyRjFURGRwVm1sU1VqbGZjVVIyYTFRM1VFRTJjVWRFYkMxNlozZFNNakpUYWtKUk1HZE9NazE2UmpCMWVVaGplVnBEZFRCcU4xZHNNV0k1WkhOalZWQndVRkZLUzFwSE5WRnRkMU14UzNoeVQzSnpVRnBhU2poNE5GbGhlR3RaTFdsVU5WVjVWbHBJTTFSTWFXb3pSMnR5WjBkbmJXMUNVWGRvUWsxQ2IyTnhPR1EwZGtRMlRIWkdaV1U1UnkxclFTNVVhRlZOUjNBNGRVaHJVRzEyZERkTFpXMTBORkYz.LdV4NLK9sxrMWOGgLRjY9PYi1EEZa1QyUzOn_EEaKcvqWJSCu39AEmQBJ1YlNOT6L7KBODhK4xvB0C6IhhptGQ; .JAVA.CLOUD.AUTH=2caa5859-7393-48f4-8748-91e9ae4494d2; .CLOUD_ACCESS_TOKEN=cn-a29f3547-6a50-44f9-b231-a4622732411b");
        for (int i = 0; i < concurrency; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    loop:
                    while (true) {
                        long now = Time.now();
//                        if (now > 1603641600000L) {
//                            break loop;
//                        }
                        try {
                            HttpClientVisitor.Result result = HttpClientVisitor.GET.invoke("http://xmgl-dev.glodon.com/system-management/org/tree?auth=true&cmd=true&__ts=" + now, headers, Charset.forName("UTF-8"));
                            long count = counter.incrementAndGet();
                            System.out.println("当前总请求次数: " + count);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
//                            try {
//                                Thread.sleep(1000L);
//                            } catch (InterruptedException e) {
//                            }
                        }
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) ;
    }
}
