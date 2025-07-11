package com.example.chatbox;

// CategoryModel class represents a model for a category in the application.
public class CategoryModel {
    // Fields to store information about the category: name, number of sets, and image URL.
    private String name;
    private int sets;
    private String url;

    // Default constructor required for Firebase serialization
    private CategoryModel() {
        // Empty constructor for Firebase
    }

    // Parameterized constructor to initialize the CategoryModel object with name, number of sets, and image URL.
    public CategoryModel(String name, int sets, String url) {
        this.name = name;
        this.sets = sets;
        this.url = url;
    }

    // Getter method to retrieve the name of the category.
    public String getName() {
        return name;
    }

    // Setter method to set the name of the category.
    public void setName(String name) {
        this.name = name;
    }

    // Getter method to retrieve the number of sets in the category.
    public int getSets() {
        return sets;
    }

    // Setter method to set the number of sets in the category.
    public void setSets(int sets) {
        this.sets = sets;
    }

    // Getter method to retrieve the image URL of the category.
    public String getUrl() {
        return url;
    }

    // Setter method to set the image URL of the category.
    public void setUrl(String url) {
        this.url = url;
    }
}
