package com.nb6868.onex.shop.modules.shop.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

// @ApiModel(value = "收货地址")
public class ReceiverDTO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "联络人", required = true)
    @NotBlank(message = "联络人不能为空")
    private String name;

    @ApiModelProperty(value = "联络电话", required = true)
    @NotBlank(message = "联络电话不能为空")
    private String tel;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "省份", required = true)
    @NotBlank(message = "省份信息不能为空")
    private String province;

    @ApiModelProperty(value = "市", required = true)
    @NotBlank(message = "市信息不能为空")
    private String city;

    @ApiModelProperty(value = "区", required = true)
    @NotBlank(message = "区信息不能为空")
    private String county;

    @ApiModelProperty(value = "区号ID", required = true)
    @NotBlank(message = "区号ID不能为空")
    private String areaCode;

    @ApiModelProperty(value = "邮政编码")
    private String postalCode;

    @ApiModelProperty(value = "详细地址", required = true)
    @NotBlank(message = "详细地址不能为空")
    private String addressDetail;

    @ApiModelProperty(value = "是否默认", required = true)
    @NotNull(message = "是否默认不能为空")
    private Integer defaultItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public Integer getDefaultItem() {
        return defaultItem;
    }

    public void setDefaultItem(Integer defaultItem) {
        this.defaultItem = defaultItem;
    }
}
