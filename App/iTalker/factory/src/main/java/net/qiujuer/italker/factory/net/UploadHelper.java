package net.qiujuer.italker.factory.net;

import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import net.qiujuer.italker.factory.Factory;

/**
 * 上传工具类，用于上传任意文件到阿里oss存储
 */
public class UploadHelper {

    private static final String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";

    // 上传的仓库名
    private static final String BUCKET_NAME = "italker-dong";

    private static OSS getClient() {

        // 在移动端建议使用STS的方式初始化OSSClient。
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAI4Fcho9YGfrbNQAKkmhhh", "t73sjthQuM3wEHVzi8KFHGAdLx4LYr");

        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);

    }

    /**
     * 上传的最终方法，成功返回一个路径
     * @param objKey 上传上去后，在服务器上独立的key
     * @param path  需要上传的文件的路径
     * @return 存储的地址
     */
    private static String upload(String objKey, String path) {
        // 构造上传请求。
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, path);

        try {

            // 初始化上传的Client
            OSS client = getClient();
            // 开始同步上传
            PutObjectResult result = client.putObject(request);
            // 得到一个网外可访问的地址
            String url = client.presignPublicObjectURL(BUCKET_NAME, objKey);
            Log.d("dongurl", "upload: presignPublicObjectURL: " + url);
            return url;

        } catch (Exception e) {
            e.printStackTrace();
            // 如果有异常则返回空
            return null;
        }

    }




}
