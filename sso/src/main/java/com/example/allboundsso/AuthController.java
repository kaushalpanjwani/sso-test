package com.example.allboundsso;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.settings.IdPMetadataParser;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;

@RestController
public class AuthController {

	@RequestMapping("/")
	public void index(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("Started");
		try {
			Saml2Settings settings = new SettingsBuilder().fromFile("onelogin.saml.properties").build();
			Map<String, Object> idpInfo = IdPMetadataParser.parseFileXML("allbound_metadata_dev.xml");
			settings = IdPMetadataParser.injectIntoSettings(settings, idpInfo);

			Auth auth = new Auth(settings, request, response);
			auth.login();

		} catch (Error | IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}



	}

	@RequestMapping("/saml/sso")
	public void acs(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Saml2Settings settings = new SettingsBuilder().fromFile("onelogin.saml.properties").build();
		Map<String, Object> idpInfo = IdPMetadataParser.parseFileXML("allbound_metadata_dev.xml");
		settings = IdPMetadataParser.injectIntoSettings(settings, idpInfo);
		
		Auth auth = new Auth(settings, request, response);
		auth.processResponse();

		List<String> errors = auth.getErrors();

		if (errors.isEmpty()) {

			Map<String, List<String>> attributes = auth.getAttributes();
			Collection<String> keys = attributes.keySet();
			for(String name :keys){
				List<String> values = attributes.get(name);
				System.out.println(name);
				for(String value :values) {
					System.out.println(value);
				}
			}
		}
	}
}
