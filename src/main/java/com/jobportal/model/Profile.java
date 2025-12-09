package com.jobportal.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "profile")
public class Profile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String headline;
    @Column(length = 2000)
    private String summary;
    private String phone;
    private String address;
    private String githubUrl;
    private String linkedinUrl;

    /**
     * We store the filename (String) in DB for profile photo & resume.
     * The controller accepts MultipartFile and writes to disk, then saves filename
     * here.
     */
    private String profilePhoto; // filename
    private String resumeFile; // filename

    private Integer experience; // years or count - adjust usage
    private Integer skillsCount;

    // optional: role/title user declares
    private String currentRole;

    // Personal Details
    private java.time.LocalDate dateOfBirth;
    private String gender;
    private String maritalStatus;
    private String permanentAddress;
    private String hometown;

    // Career Profile
    private String currentIndustry;
    private String functionalArea;
    private String roleCategory;
    private String desiredJobType; // Permanent, Contractual
    private String desiredEmploymentType; // Full Time, Part Time
    private String preferredShift; // Day, Night, Flexible
    private String expectedSalary;
    private String desiredLocation;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors
    public Profile() {
    }

    // getters & setters (generated)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getResumeFile() {
        return resumeFile;
    }

    public void setResumeFile(String resumeFile) {
        this.resumeFile = resumeFile;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getSkillsCount() {
        return skillsCount;
    }

    public void setSkillsCount(Integer skillsCount) {
        this.skillsCount = skillsCount;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }

    // personal details getters/setters
    public java.time.LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(java.time.LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    // career profile getters/setters
    public String getCurrentIndustry() {
        return currentIndustry;
    }

    public void setCurrentIndustry(String currentIndustry) {
        this.currentIndustry = currentIndustry;
    }

    public String getFunctionalArea() {
        return functionalArea;
    }

    public void setFunctionalArea(String functionalArea) {
        this.functionalArea = functionalArea;
    }

    public String getRoleCategory() {
        return roleCategory;
    }

    public void setRoleCategory(String roleCategory) {
        this.roleCategory = roleCategory;
    }

    public String getDesiredJobType() {
        return desiredJobType;
    }

    public void setDesiredJobType(String desiredJobType) {
        this.desiredJobType = desiredJobType;
    }

    public String getDesiredEmploymentType() {
        return desiredEmploymentType;
    }

    public void setDesiredEmploymentType(String desiredEmploymentType) {
        this.desiredEmploymentType = desiredEmploymentType;
    }

    public String getPreferredShift() {
        return preferredShift;
    }

    public void setPreferredShift(String preferredShift) {
        this.preferredShift = preferredShift;
    }

    public String getExpectedSalary() {
        return expectedSalary;
    }

    public void setExpectedSalary(String expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public String getDesiredLocation() {
        return desiredLocation;
    }

    public void setDesiredLocation(String desiredLocation) {
        this.desiredLocation = desiredLocation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
