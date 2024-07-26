package com.ly.gpt.generater.img;


import com.ly.gpt.generater.ContentOutput;

import java.util.List;

public interface Text2PhotoOutput extends ContentOutput {

    List<String> getPhotos();

}
