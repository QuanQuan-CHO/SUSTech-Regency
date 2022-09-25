package com.sustech.regency.model.param;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindPasswordParam {
	@ApiModelProperty(value="验证码",required=true,example="\"114514\"")
	@Size(min=6,max=6,message="验证码必须为6位")
	String verificationCode;

	@ApiModelProperty(required=true)
	@Email(message = "邮箱格式错误")
	@NotEmpty(message = "邮箱不能为空")
	String email;

	@ApiModelProperty(required = true)
	@Size(min=8,max=30,message = "密码需要为8-30位")
	String newPassword;
}