package com.example.demoteste;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.example.demoteste.usuario.UsuarioController;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DemoTesteApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UsuarioController usuarioController;

	/**
	 * teste de verificação de integridade simples que falhará se o contexto do
	 * aplicativo (applicationContext) do Spring não puder ser iniciado.
	 */
	@Test
	void contextLoads() {
	}

	/**
	 * teste para se convencer de que o contexto está criando o controller.
	 */
	@Test
	void smokeTest() {
		assertThat(usuarioController).isNotNull();
	}

	/**
	 * teste de sanidade, iniciar o aplicativo e ouvir uma conexão (como faria na
	 * produção) e em seguida, enviar uma solicitação HTTP e declarar a resposta.
	 * 
	 * @throws JSONException
	 */
	@Test
	public void httpRequestTest() throws JSONException {
		String respostaAtual = restTemplate
				.getForObject("http://localhost:" + port + "/api/v1/usuarios", String.class);
		String respostaEsperada = "{\"_links\":{\"self\":{\"href\":\"http://localhost:" + port
				+ "/api/v1/usuarios\"}}}";

		assertThat(respostaAtual).isEqualToIgnoringNewLines(respostaEsperada);
		JSONAssert.assertEquals(respostaEsperada, respostaAtual, false);
	}
}
