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
        return "aa2";
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






//    private final NewBoardService boardFileService;
//    private final FileService fileService;

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

}