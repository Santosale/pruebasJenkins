
/* Comunes */
function askSubmission(msg, form) {
	if (confirm(msg))
		form.submit();
}
			
function relativeRedir(loc) {	
	var b = document.getElementsByTagName('base');
	if (b && b[0] && b[0].href) {
		if (b[0].href.substr(b[0].href.length - 1) == '/' && loc.charAt(0) == '/')
		loc = loc.substr(1);
		loc = b[0].href + loc;
	}
	window.location.replace(loc);
}

/* Trips list.jsp */

// Función para internacionalizar el precio
function internacionalizationPrice (price, language) {
	var priceSplited = String(price).split(".");
	
	if (language == "es") {
		price = priceSplited[0];
		if(priceSplited[1] !== undefined){
			price += "," + priceSplited[1];
		}
	}
	
	return price;
}

/* Curriculum list.jsp */

// Función para internacionalizar la fecha
function internacionalizationDate (date, language) {
	var separate;
	var res;

	res = "";

	if (date == null) {
		res = "-";
	} else {

		separate = date.split("-");
		if (language == "es") {
			res = separate[2] + "/" + separate[1] + "/" + separate[0];

		} else if (language == "en") {
			res = separate[1] + "/" + separate[2] + "/" + separate[0];
		}
	}

	return res;
};

// Función para pedir records
function getRecords(idButton, idContainer, url, curriculumId) {
	$(idButton).click(function() {

		if ($(idButton).next().hasClass('display-on')) {
			$(idButton).next().removeClass('display-on');
			$(idContainer).removeClass("container-square2");
			$(idContainer).empty();
		} else {
			// Vemos que record ha realizado la petición y lo dirigimos a su función
			var parameters = { curriculumId : curriculumId, page : 1, size : 5 };
			if (idButton == "#professional") {
				$.get(url, parameters, requestProfessional);

			} else if (idButton == "#miscellaneous") {
				$.get(url, parameters, requestMiscellaneous);

			} else if (idButton == "#endorser") {
				$.get(url, parameters, requestEndorser);

			} else {
				$.get(url, parameters, requestEducation);
			}

		}

	});
};
