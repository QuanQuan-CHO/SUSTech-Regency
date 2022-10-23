package com.sustech.regency.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class RegisterParam {
	@ApiModelProperty(value="角色, 1为消费者, 2为商家",allowableValues="1,2",required=true,example="1")
	@Range(min=1,max=2,message="roleId只能是1或2")
	Integer roleId;

	@ApiModelProperty(value="验证码",required=true,example="114514")
	@Size(min=6,max=6,message="验证码必须为6位")
	String verificationCode;

	@ApiModelProperty(required=true)
	@Email(message = "邮箱格式错误")
	String email;

	@ApiModelProperty(value = "用户名", required = true)
	@NotEmpty(message = "Username shouldn't be null")
	private String username;

	@ApiModelProperty(required = true, example = "***********")
	@Size(min=8, max=30, message = "密码需要为8-30位")
	@NotEmpty
	private String password;
}