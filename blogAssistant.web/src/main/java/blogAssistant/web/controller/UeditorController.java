package blogAssistant.web.controller;

import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yuananyun on 2016/8/21.
 */
@Controller
@RequestMapping("/ueditor")
public class UeditorController {
    @Value("${ueditor.image.upload.folder}")
    private String imageSaveFolder;

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    @ResponseBody
    public void upload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if ("config".equals(action)) {
            OutputStream os = response.getOutputStream();
            IOUtils.copy(UeditorController.class.getClassLoader().getResourceAsStream("ueditor_config.json"), os);
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> upload(HttpServletRequest request, @RequestParam(value = "action") String action, @RequestParam("upfile") MultipartFile[] uploadFiles) throws IOException {
        MultipartFile upfile = uploadFiles[0];
        Map<String, String> result = Maps.newHashMap();
        String path = saveUploadFile(upfile);
        String state = "SUCCESS";
        File file = new File(path);
        //返回类型
        String rootPath = request.getContextPath();
        result.put("url", rootPath + "/ueditor/show?filePath=" + path);
        result.put("size", String.valueOf(file.length()));
        result.put("type", file.getName().substring(file.getName().lastIndexOf(".")));
        result.put("state", state);
        return result;
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public void show(String filePath, HttpServletResponse response) throws IOException {
        File file = new File(filePath);
        response.setDateHeader("Expires", System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        response.setHeader("Cache-Control", "max-age=60");
        OutputStream os = response.getOutputStream();
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            IOUtils.copy(is, os);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
            return;
        } finally {
            if (null != is) {
                is.close();
            }
            if (null != os) {
                os.flush();
                os.close();
            }
        }
    }

    protected String saveUploadFile(MultipartFile uploadFile) {
        String absolutePath = imageSaveFolder;
        File folder = new File(absolutePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String rawName = uploadFile.getOriginalFilename();
        String fileExt = rawName.substring(rawName.lastIndexOf("."));
        String newName = System.currentTimeMillis() + UUID.randomUUID().toString() + fileExt;
        String fileSavePath = absolutePath + File.separator + newName;
        File saveFile = new File(fileSavePath);
        try {
            uploadFile.transferTo(saveFile);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return fileSavePath;
    }


}
