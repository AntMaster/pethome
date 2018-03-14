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


    @PutMapping(value = "/publish")
    public ResultVO upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        String fileName = (System.currentTimeMillis() + file.getOriginalFilename()).replaceAll(";", "");
        //String filePath = request.getSession().getServletContext().getRealPath("")  + "WEB-INF" + File.separator + "classes" + File.separator;
        String filePath =  picturePath + File.separator + "picture" + File.separator + "publish" +  File.separator  ;
        try {
            FileUtil.uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            throw new PetHomeException(ResultEnum.FAILURE.getCode(), "文件上传失败");
        }

        String path  = "upload/picture/publish/" + fileName;
        return ResultVOUtil.success(path);
    }

/*

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {


        String fileName = (System.currentTimeMillis() + file.getOriginalFilename()).replaceAll(";", "");

        String filePath = request.getSession().getServletContext().getRealPath("")  + "upload" + File.separator + "publish" + File.separator;
        try {
            FileUtil.uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            throw new PetHomeException(ResultEnum.FAILURE.getCode(), "文件上传失败");
        }

        return "/upload/publish/" + fileName;
    }

*/



    @PostMapping("/publish/save")
    public String savePetImg(@RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                /*
                 * 这段代码执行完毕之后，图片上传到了工程的跟路径； 大家自己扩散下思维，如果我们想把图片上传到
                 * d:/files大家是否能实现呢？ 等等;
                 * 这里只是简单一个例子,请自行参考，融入到实际中可能需要大家自己做一些思考，比如： 1、文件路径； 2、文件名；
                 * 3、文件格式; 4、文件大小的限制;
                 */
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(file.getOriginalFilename())));
                System.out.println(file.getName());
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            }

            return "上传成功";

        } else {
            return "上传失败，因为文件是空的.";
        }
    }


}
