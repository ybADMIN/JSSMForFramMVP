package com.huanmedia.jssm.controller;

import com.huanmedia.jssm.pojo.FileEntity;
import com.huanmedia.jssm.pojo.User;
import com.huanmedia.jssm.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import view.ByteRangeViewRender;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("/download")
// /user/**
public class DownController {
    private static Logger log = LoggerFactory.getLogger(DownController.class);
    int ReadBufferSize = 1024 * 10;
   public final static ThreadLocal<SimpleDateFormat> simpleDataThreadLocal = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return  new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        }
    };
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public @ResponseBody  JasonResponseBody getFileList(HttpServletRequest request) {
        String realPath = request.getSession().getServletContext().getRealPath("/res/reouseFile/");
        File file = new File(realPath);
        File[] files = file.listFiles();
        List<FileEntity> fileEntities = new ArrayList<FileEntity>();
        for (int i = 0; i < files.length; i++) {
            FileEntity fe = new FileEntity();
            String localUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/download/file";
            fe.setFixName(files[i].getName());
            fe.setUrl(localUrl + "?filename=" + files[i].getName());
            fileEntities.add(fe);
        }
        JasonResponseBody<List<FileEntity>> responseBody = new JasonResponseBody<List<FileEntity>>();
        responseBody.setResult(fileEntities);
        return responseBody;
    }

    @RequestMapping(value = "/file", method = {RequestMethod.GET, RequestMethod.HEAD})
    public HttpServletResponse getApp(HttpServletRequest request, HttpServletResponse response) {
        String filename = request.getParameter("filename");
        SimpleDateFormat sdf = simpleDataThreadLocal.get();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            String realPath = request.getSession().getServletContext().getRealPath("/res/reouseFile/" + filename);
            File file = new File(realPath);
            response.reset();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Accept-Ranges", "bytes");
            String lastmodify = sdf.format(new Date(file.lastModified()));
            response.setHeader("Last-Modified",lastmodify);
            long fSize = file.length();
            String guessCharset = "UTF-8";
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(file.getName().getBytes(guessCharset), "iso-8859-1"));
            String modifiedstr = request.getHeader("If-Modified-Since");
            long pos = 0;
            long endPos = fSize;
            if (null != request.getHeader("Range")) {
                // 断点续传
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                try {
                    String[] ranges = request.getHeader("Range").replace("bytes=", "").split("-");
                    pos = Long.parseLong(ranges[0]);
                    if (ranges.length > 1) {
                        long rangesEnd = Long.parseLong(ranges[1]);
                        if (rangesEnd < endPos) {
                            endPos = rangesEnd;
                        }
                    }
                } catch (NumberFormatException e) {
                    pos = 0;
                }
            }
            String contentRange = new StringBuffer("bytes ").append(pos + "").append("-").append(endPos + "").append("/").append(fSize + "").toString();
//            System.out.println(String.format("读取位置：pos:%d posEnd:%d posLength: %d", pos, endPos, endPos-pos));
            long rangeStart = (pos) > 0 ? pos: 0;
            long rangeEnd=endPos+1 > file.length() ? endPos : endPos+1 ;
            long rangeLength=rangeEnd-rangeStart>0?rangeEnd-rangeStart:0;
            response.setHeader("Content-Length", String.valueOf(rangeLength));
            response.setHeader("Content-Range", contentRange);
            if (modifiedstr != null) {
                Date date = sdf.parse(modifiedstr);
                if (date.getTime() == file.lastModified()) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                }
            } else {
                if (!request.getMethod().equals(RequestMethod.HEAD.name())) {
                    outFile(request, response, file, rangeLength,rangeStart,rangeEnd);
                }
            }
        } catch (IOException e) {
           System.err.println(e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void outFile(HttpServletRequest request, HttpServletResponse response, File file, long rangesLenght, long rangesStart, long reangesEnd) throws IOException {
        RandomAccessFile in = null;
        ServletOutputStream out = null;
        try {
            in = new RandomAccessFile(file.getPath(), "r");
            out = response.getOutputStream();
            in.seek(rangesStart);
            byte[] buffer;
            buffer = new byte[ReadBufferSize];
            int readSize = 0;
            int length = 0;
            if (rangesLenght > 0)
                while ((length = in.read(buffer, 0, buffer.length)) != -1 && rangesLenght > 0 && readSize < rangesLenght) {
                    readSize += length;
                    out.write(buffer, 0, length);
                    // Thread.sleep(100);
                }
//            System.out.println(String.format("请求方法：%s", request.getMethod()));
//            System.out.println(String.format("请求Rangs：%s", request.getHeader("Range")));
//            System.out.println(String.format("读取位置：rangesStart:%d reangesEnd:%d readLength: %d", rangesStart, reangesEnd, rangesLenght));
//            System.out.println(String.format("返回字节长度：%d Content-Length:%s", readSize, response.getHeader("Content-Length")));
        } finally {
            if (in != null)
                in.close();
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    static class JasonResponseBody<T> {
        String code = "1";
        String message = "成功";
        T result;

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}