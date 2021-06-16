package com.abhishek.weatherapp.Adapters;

/**The class that'll be used in the 7 day report**/
public class ItemWeatherReport {
    String minTemp, maxTemp, imageUrl, day, imgText;

    public ItemWeatherReport(String minTemp, String maxTemp, String imageUrl, String day, String imgText) {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.imageUrl = imageUrl;
        this.day = day;
        this.imgText = imgText;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDay() {
        return day;
    }

    public String getImgText() {
        return imgText;
    }
}
