package com.its.file;

import com.its.file.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ImageService imageService;
        @GetMapping("/")
        public String index () {
            return "file4";
        }

        @PostMapping("/save")
        public String save(@ModelAttribute ImageDTO imageDTO, MultipartHttpServletRequest mp, Model model) throws IOException {
            imageService.save(imageDTO);
            List<MultipartFile> multipartFileList = mp.getFiles("imageFile");


        return "redirect:/";
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
