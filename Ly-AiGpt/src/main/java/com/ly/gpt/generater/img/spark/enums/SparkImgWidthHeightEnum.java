package com.ly.gpt.generater.img.spark.enums;

/**
 * @author zhongl
 * @date 2024/06/21 15:04
 */
public enum SparkImgWidthHeightEnum {

    WH_512_512(512, 512),
    WH_640_360(640, 360),
    WH_640_480(640, 480),
    WH_640_640(640, 640),
    WH_680_512(680, 512),
    WH_512_680(512, 680),
    WH_768_768(768, 768),
    WH_720_1280(720, 1280),
    WH_1280_720(1280, 720),
    WH_1024_1024(1024, 1024);
    private Integer width;
    private Integer height;

    SparkImgWidthHeightEnum(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }


    public static Integer getHeightByWidth(Integer width) {
        for (SparkImgWidthHeightEnum sparkImgWidthHeightEnum : SparkImgWidthHeightEnum.values()) {
            if (sparkImgWidthHeightEnum.width.equals(width)) {
                return sparkImgWidthHeightEnum.height;
            }
        }

        return null;
    }

}
