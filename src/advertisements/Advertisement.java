/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advertisements;

/**
 *
 * @author Ammar
 */
public class Advertisement {
    private int id;
    private String image_url;
    private int duration;
    
    public int getId() {
        return id;
    }
    
    public String getImageUrl() {
        return image_url;
    }
    
    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Advertisement{" + "id=" + id + ", img_url=" + image_url + ", duration=" + duration + '}';
    }
    
}
