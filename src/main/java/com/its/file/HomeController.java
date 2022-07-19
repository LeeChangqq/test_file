package com.its.file;

import com.its.file.service.ImageService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.concurrent.TimeUnit;



@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ImageService imageService;
    @GetMapping("/")
    public String index () {
        return "w";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ImageDTO imageDTO, MultipartHttpServletRequest mp, Model model) throws IOException {
        imageService.save(imageDTO);
        List<MultipartFile> multipartFileList = mp.getFiles("imageFile");
        return "redirect:/";
    }

    @GetMapping("/findById/{id}")
    public String findById(@PathVariable Long id, Model model) {
        ImageDTO imageDTO = imageService.findById(id);
        model.addAttribute("image",imageDTO);
        return "detail";
    }


    private final String TEMPLATE_DIR = "video/";
    private final String UPLOAD_DIR = "D:\\upload\\";

    // 1. 웹경로에 의한 스트리밍
    // 스프링프로젝트의 /resources/static/video 폴터에 동영상을 넣어 서비스
    @GetMapping("")
    public String videoIndex(Model model) {
        log.debug("************** class = {}, function = {}", this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        model.addAttribute("videoUrl", "1657680268330_20220712_135103.mp4");
        return TEMPLATE_DIR + "video";
    }


    // 2. 다운로드 방식으로 서비스
    // byte array 로 내려주는 경우 스트리밍이 아닌 다운로드 방식이므로 전체 파일이 다운로드 된 이후 플레이 된다.
    // 크롬인 경우 플에이 이후 시간 설정이 되지 않는다.
    @GetMapping("/download")
    public String videoDownload(Model model) {
        log.debug("************** class = {}, function = {}", this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        model.addAttribute("videoUrl", "1657680268330_20220712_135103.mp4");
        return TEMPLATE_DIR + "video";
    }

    @GetMapping("/download/{fileName}")
    public void vidoeDownloadFileName(@PathVariable String fileName, HttpServletRequest req, HttpServletResponse res) throws IOException {
        log.debug("************** class = {}, function = {}", this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());


        String fileFullPath = UPLOAD_DIR + fileName;
        File downFile = new File(fileFullPath);  //파일 객체 생성
        if(downFile.isFile()){  // 파일이 존재하면
            int fSize = (int)downFile.length();
            res.setBufferSize(fSize);
            res.setContentType("application/octet-stream");
            res.setHeader("Content-Disposition", "attachment; filename="+fileName+";");
            res.setContentLength(fSize);  // 헤더정보 입력
            FileInputStream in  = new FileInputStream(downFile);
            ServletOutputStream out = res.getOutputStream();
            try
            {
                byte[] buf=new byte[8192];  // 8Kbyte 로 쪼개서 보낸다.
                int bytesread = 0, bytesBuffered = 0;
                while( (bytesread = in.read( buf )) > -1 ) {
                    out.write( buf, 0, bytesread );
                    bytesBuffered += bytesread;
                    if (bytesBuffered > 1024 * 1024) { //아웃풋스트림이 1MB 가 넘어가면 flush 해준다.
                        bytesBuffered = 0;
                        out.flush();
                    }
                }
            }
            finally {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                //에러가 나더라도 아웃풋 flush와 close를 실행한다.
            }
        }

    }

    // 3. 동영상 파일을 Resource 객체로 내려주는 경우
    // 파일이 전체 내려오지 않아도 동영상 플레이가 시작된다. 단 중간에 플레이 구간을 선택 시 다시 파일을 받기 시작한다.
    @GetMapping("/resource")
    public String videoResource(Model model) {
        log.debug("************** class = {}, function = {}", this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        model.addAttribute("videoUrl", "1657680268330_20220712_135103.mp4");
        return TEMPLATE_DIR + "video";
    }

    @GetMapping("/resource/{fileName}")
    public ResponseEntity<Resource> vidoeResourceFileName(@PathVariable String fileName) throws FileNotFoundException {
        log.debug("************** class = {}, function = {}", this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        String fileFullPath = UPLOAD_DIR + fileName;
        Resource resource = new FileSystemResource(fileFullPath);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + "");
        headers.setContentType(MediaType.parseMediaType("video/mp4"));
        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }


    // 4. 범위설정을 하여 파일의 일부분만 내려주는 형식
    // 동영상 시작시 설정된 파일 크기를 내리고 video 태그에서 호출하는 단위 크기만큼만 계속 내려주는 형식
    // 플레이 구간을 선택 시 해당하는 구간부터 파일을 내려받는다.
    @GetMapping("/region")
    public String videoRegion(Model model) {
        log.debug("************** class = {}, function = {}", this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        model.addAttribute("videoUrl", "1657680268330_20220712_135103.mp4");
        return TEMPLATE_DIR + "video";
    }

    @GetMapping("/region/{fileName}")
    public ResponseEntity<ResourceRegion> vidoeRegionFileName(@PathVariable String fileName, @RequestHeader HttpHeaders headers) throws IOException {
        log.debug("************** class = {}, function = {}", this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        String fileFullPath = UPLOAD_DIR + fileName;
        Resource resource = new FileSystemResource(fileFullPath);

        final long chunkSize = 1024 * 1024 * 1;
        long contentLength = resource.contentLength();
        ResourceRegion region;

        try {
            HttpRange httpRange = headers.getRange().stream().findFirst().get();
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = Long.min(chunkSize, end - start + 1);
            region = new ResourceRegion(resource, start, rangeLength);

            log.debug("**********  httpRange :: rangeLength ==> {}", rangeLength);
        } catch (Exception e) {
            long rangeLength = Long.min(chunkSize, contentLength);
            region = new ResourceRegion(resource, 0, rangeLength);
            log.debug("**********  httpRange error :: rangeLength ==> {}", rangeLength);
        }
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header("Accept-Ranges", "bytes")
                .eTag(fileName) // IE 부분 호출을 위해서 설정
                .body(region);
    }





    //    private final NewBoardService boardFileService;
//    private final FileService fileService;
//
//    @GetMapping("/fileSave-form")
//    public String fileSaveForm() {
//        return "board/fileSave";
//    }
//    @PostMapping("/fileSave")
//    public String fileSave(@ModelAttribute NewBoardDTO boardFileDTO,
//                           MultipartHttpServletRequest mp,
//                           Model model) throws IOException {
//        NewBoardDTO saveDTO = boardFileService.fileSave(boardFileDTO);
//        List<MultipartFile> multipartFileList = mp.getFiles("boardFile");
//
//        List<FileDTO> fileDTOList = new ArrayList<>();
//        for (MultipartFile m: multipartFileList) {
//            FileDTO fileDTO = new FileDTO();
//            fileDTO.setBoardId(saveDTO.getId());
//            fileDTO.setBoardFile(m);
//            fileDTOList.add(fileService.save(fileDTO));
//        }
//        saveDTO.setBoardFileList(fileDTOList);
//        model.addAttribute("boardDTO", saveDTO);
////        saveDTO.getBoardDTOList.get(i);
////        model.addAttribute("fileDTOList", fileDTOList);
//        return "board/detail";
//
}