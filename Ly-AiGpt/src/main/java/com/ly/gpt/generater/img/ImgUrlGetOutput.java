package com.ly.gpt.generater.img;


import com.ly.gpt.entity.vo.ResponseEntity;
import lombok.Data;

import java.util.List;

@Data
public class ImgUrlGetOutput extends ResponseEntity {


    private  String prompt;
    private  String status;
    private  String error;

    private List<String> imageUrls;


}
