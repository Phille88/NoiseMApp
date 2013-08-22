<?php
 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';

if(isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY){
	 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';
	 
	// connecting to db
	$db = new DB_CONNECT();
	 
	// get all products from products table
	$result = mysql_query("SELECT *FROM noiserecordings") or die(mysql_error());
	 
	// check for empty result
	if (mysql_num_rows($result) > 0) {
		// looping through all results
		// products node
		$response["noiserecordings"] = array();
	 
		while ($row = mysql_fetch_array($result)) {
			// temp user array
			$noiserecording = array();
				$noiserecording["noiseID"] = $row["noiseID"];
				$noiserecording["userID"] = $row["userID"];
				$noiserecording["latitude"] = $row["latitude"];
				$noiserecording["longitude"] = $row["longitude"];
				$noiserecording["dB"] = $row["dB"];
				$noiserecording["accuracy"] = $row["accuracy"];
				$noiserecording["quality"] = $row["quality"];
				$noiserecording["timeStamp"] = $row["timeStamp"];
	 
			// push single product into final response array
			array_push($response["noiserecordings"], $noiserecording);
		}
		// success
		$response["success"] = 1;
	 
		// echoing JSON response
		echo json_encode($response);
	} else {
		// no products found
		$response["success"] = 0;
		$response["message"] = "No user profiles found";
	 
		// echo no users JSON
		echo json_encode($response);
	}
}
else{
	// required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>