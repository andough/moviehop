/**

 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");
    debugger;
    // populate the star info h3
    // find the empty h3 body by id "star_info"
//    let starInfoElement = jQuery("#star_info");
//
//    // append two html <p> created to the h3 body, which will refresh the page
//    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
//        "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");
    var resultData2 = [];
	$.each(resultData, function(index, value){
		var arrStars = value.star.split(',');
		var arrStarIds = value.starid.split(',');
		//var arrGenres = value.genres.spilt(',');
		
		value.movielink = "<a target='_blank' href='single-movie.html?id=" + value.movieid + "'>" + value.title + "</a>";		
		var step;
		var step1;
		value.starlink = "";
		for (step = 0; step < arrStars.length; step++){
			value.starlink += "<a href='single-star.html?id=" + arrStarIds[step] + "'>" + arrStars[step] + "</a><br>";
		} 
//		for (step1 = 0; step1 < arrGenres.length; step1++) {
//			value.genrelink ="<a target='_blank' href='browse.html?genres=" + arrGenres[step1] + "'>" + arrGenres[step1] + "</a>";
		//}// target='_blank'
		
		resultData2.push(value);
	});

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData2[i]["starlink"] +  '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["genres"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});