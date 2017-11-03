import sun.rmi.runtime.Log;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/6/7.
 */
public class Main {
    private static int ReadBufferSize=1;

    private static String extension(String url) {
        String suffixes="avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc";
        Pattern pat=Pattern.compile("[^\\ /:*？\"<>|]+[\\.][\\w]+");//匹配文件名
        Pattern extension=Pattern.compile("[\\.][\\w]+");//后缀
        Matcher mc=pat.matcher(url);//条件匹配
        String substring = "";
        while(mc.find()){
            substring = mc.group();//截取文件名后缀名
        }
        Matcher exmc= extension.matcher(substring);
        while (exmc.find()){
            System.out.println(exmc.group(exmc.groupCount()));
        }
        return substring;
    }

    public static void main(String args[]) {
        AtomicInteger mOpenCounter = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            mOpenCounter.incrementAndGet();
            System.out.println(mOpenCounter.intValue());
        }
    }

    public static String matcherName( String url) {
        Pattern pat=Pattern.compile("[^\\ /:*？\"<>|]+[\\.][\\w]+");//匹配文件名
        Matcher mc=pat.matcher(url);//条件匹配
        String substring = "";
        while(mc.find()){
            substring = mc.group();//截取文件名
        }
        if (substring.equals("")){
            String[] name = url.split("/");
            if (name.length>0){
                substring=name[name.length-1];
            }
        }
        return substring;
    }
    private static void outFile(FileOutputStream outputStream,File file, long pos, long endPos) throws IOException {
        RandomAccessFile in = null;
        FileOutputStream out = null;
        try {
            in=new RandomAccessFile(file.getPath(),"r");
            out= outputStream;
            pos=(pos-1)>0?pos-1:0;
            in.seek(pos);
            byte[] buffer;

            long readLength = (endPos+1>file.length()?file.length():endPos+1 - pos);
            if (readLength<0)
                readLength=0;

            buffer = new byte[ReadBufferSize];
            int readSize=0;
            int length = 0;
            if (readLength>0)
                while ((length = in.read(buffer, 0, buffer.length)) != -1&& readLength>0 && readSize<readLength) {
                    readSize+=length;
                    out.write(buffer, 0, length);
                    // Thread.sleep(100);
                }
            System.out.println(String.format("读取位置：pos:%d endPos:%d readLength: %d",pos,endPos,readLength));
            System.out.println(String.format("返回字节长度：%d Content-Length:%d",readSize,endPos - pos));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in!=null)
                in.close();
            if (out!=null){
                out.flush();
                out.close();
            }
        }
    }
}
