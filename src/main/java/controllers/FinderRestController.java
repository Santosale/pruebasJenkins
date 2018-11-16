/*
 * WelcomeController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import services.TripService;
import domain.Trip;

/*
 * La anotaci�n @RestController es una variante de la anotaci�n @Controller
 * que a�ade, entre otras cosas, la anotaci�n @ResponseBody a todos los m�todos.
 * 
 * @ResponseBody es una anotaci�n que espec�fica que el m�todo no va a devolver una vista
 * sino un objeto para que de esta forma sea automaticamente serializando a JSON.
 */
@RestController
@RequestMapping("/finder")
public class FinderRestController extends AbstractController {

	// Service
	@Autowired
	private TripService	tripService;


	// Constructors -----------------------------------------------------------

	public FinderRestController() {
		super();
	}

	/*
	 * El m�todo search el que utilicemos como endpoint
	 * para realizar peticiones desde la vista utilizando el c�digo JavaScript.
	 * Este m�todo recibir� por la URL la variable keyword introducida desde la vista
	 * y devolver� un objeto de tipo ResponseEntity, el cual servir� para pasar
	 * la informaci�n requerida por HTTP, adem�s de c�digos de estado.
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> search(@RequestParam final String keyword) {
		Collection<Trip> trips;
		ResponseEntity<?> response;
		Map<String, Object> resume;

		// Realmente el objeto que devolveremos es una lista de mapas,
		// puesto que los mapas son coleciones que se organizan clave -> valor
		// como un JSON, y la lista ser� el equivalente del Array en JSON.
		// De esta forma al serializarlo aparecer� el JSON que queremos.
		List<Map<String, Object>> result;

		result = new ArrayList<Map<String, Object>>();

		// Obtenemos los viajes seg�n la palabra clave que le
		// hemos pasado por par�metro.
		trips = this.tripService.findByKeyWord(keyword);

		// Para no devolver una cantidad de informaci�n innecesaria
		// y as� optimizar, elegimos las cinco propiedades b�sicas
		// para mostrar el viaje.
		for (final Trip t : trips) {
			resume = new HashMap<String, Object>();

			resume.put("id", t.getId());
			resume.put("title", t.getTitle());
			resume.put("description", t.getDescription());
			resume.put("price", t.getPrice());
			resume.put("explorerRequirements", t.getExplorerRequirements());
			resume.put("locale", LocaleContextHolder.getLocale().toLanguageTag());


			result.add(resume);
		}
		
		// Metemos el resultado en el ResponseEntity y el estatus HTTP
		// lo pasamos como OK (200).
		response = new ResponseEntity<List<Map<String, Object>>>(result, HttpStatus.OK);

		return response;
	}

}
