package com.shumahe.pethome.Controller;

import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Util.FileUtil;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@RestController
@Slf4j
@RequestMapping("/upload")
public class FileController {


    //request.getRealPath() 过时
    /*request.getSession().getServletContext().getRealPath("/")
      在Servlet 里用this.getServletContext().getRealPath("/");获得绝对路径。
        struts里用this.getServlet().getServletContext().getRealPath("/")获得绝对路径。
    */

    //
    /**
     * 发布上传图片
     * @param file
     * @param request
     * @return
     */

    @Value("${picturePath}")
    private  String picturePath;

    /**
     * 发布上传图片
     * @param file
     * @param
     * @return
     */
    @PutMapping(value = "/publish")
    public ResultVO upload(@RequestParam("file") MultipartFile file) {

        String fileName = (System.currentTimeMillis() + file.getOriginalFilename()).replaceAll(";", "");
        String filePath =  picturePath + File.separator + "picture" + File.separator + "publish" +  File.separator  ;
        try {
            FileUtil.uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            throw new PetHomeException(ResultEnum.FAILURE.getCode(), "文件上传失败");
        }

        String path  = "upload/picture/publish/" + fileName;
        return ResultVOUtil.success(path);
    }


    /**
     * 发布上传图片
     * @param file
     * @param
     * @return
     */
    @PutMapping(value = "/auth")
    public ResultVO authUpload(@RequestParam("file") MultipartFile file) {

        String fileName = (System.currentTimeMillis() + file.getOriginalFilename()).replaceAll(";", "");
        String filePath =  picturePath + File.separator + "picture" + File.separator + "auth" +  File.separator  ;
        try {
            FileUtil.uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            throw new PetHomeException(ResultEnum.FAILURE.getCode(), "文件上传失败");
        }

        String path  = "upload/picture/auth/" + fileName;
        return ResultVOUtil.success(path);
    }


    /**
     * 宠卡上传头像
     * @param file
     * @param
     * @return
     */
    @PutMapping(value = "/pet")
    public ResultVO petUpload(@RequestParam("file") MultipartFile file) {

        String fileName = (System.currentTimeMillis() + file.getOriginalFilename()).replaceAll(";", "");
        String filePath =  picturePath + File.separator + "picture" + File.separator + "pet" +  File.separator  ;
        try {
            FileUtil.uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            throw new PetHomeException(ResultEnum.FAILURE.getCode(), "文件上传失败");
        }

        String path  = "upload/picture/pet/" + fileName;
        return ResultVOUtil.success(path);
    }

  /*  @PutMapping(value = "/pet")
    public ResultVO petUpload(@RequestParam("file") MultipartFile file) {

        String fileName = (System.currentTimeMillis() + file.getOriginalFilename()).replaceAll(";", "");
        //String filePath = request.getSession().getServletContext().getRealPath("")  + "WEB-INF" + File.separator + "classes" + File.separator;
        String filePath =  picturePath + File.separator + "picture" + File.separator + "publish" +  File.separator  ;
        try {
            FileUtil.uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            throw new PetHomeException(ResultEnum.FAILURE.getCode(), "文件上传失败");
        }

        String path  = "upload/picture/pet/" + fileName;
        return ResultVOUtil.success(path);
    }
*/


}
