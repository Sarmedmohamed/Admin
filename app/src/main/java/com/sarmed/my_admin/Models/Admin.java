package com.sarmed.my_admin.Models;

import java.io.Serializable;

public class Admin implements Serializable {
    // getter method for our id
    public String getId() {
        return id;
    }
    // setter method for our id
    public void setId(String id) {
        this.id = id;
    }


    private String id;
    private int idSpinner;
    private String filePath , departmentName,nameMeal,caloriesMeal,descriptionMeal;

    public Admin(int idSpinner, String filePath, String departmentName, String nameMeal, String caloriesMeal, String descriptionMeal) {
        this.idSpinner = idSpinner;
        this.filePath = filePath;
        this.departmentName = departmentName;
        this.nameMeal = nameMeal;
        this.caloriesMeal = caloriesMeal;
        this.descriptionMeal = descriptionMeal;
    }
    public Admin(String id , int idSpinner, String filePath, String departmentName, String nameMeal, String caloriesMeal, String descriptionMeal) {
        this.id = id;
        this.idSpinner = idSpinner;
        this.filePath = filePath;
        this.departmentName = departmentName;
        this.nameMeal = nameMeal;
        this.caloriesMeal = caloriesMeal;
        this.descriptionMeal = descriptionMeal;
    }
    public Admin(String filePath, String departmentName, String nameMeal, String caloriesMeal, String descriptionMeal) {
        this.filePath = filePath;
        this.departmentName = departmentName;
        this.nameMeal = nameMeal;
        this.caloriesMeal = caloriesMeal;
        this.descriptionMeal = descriptionMeal;
    }

    public int getIdSpinner() {
        return idSpinner;
    }

    public void setIdSpinner(int idSpinner) {
        this.idSpinner = idSpinner;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Admin() {
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getNameMeal() {
        return nameMeal;
    }

    public void setNameMeal(String nameMeal) {
        this.nameMeal = nameMeal;
    }

    public String getCaloriesMeal() {
        return caloriesMeal;
    }

    public void setCaloriesMeal(String caloriesMeal) {
        this.caloriesMeal = caloriesMeal;
    }

    public String getDescriptionMeal() {
        return descriptionMeal;
    }

    public void setDescriptionMeal(String descriptionMeal) {
        this.descriptionMeal = descriptionMeal;
    }
}
