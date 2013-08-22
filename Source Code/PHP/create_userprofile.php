<?php
 
/*
 * Following code will create a new profile row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();


require_once '/home/thesis-std/request_key.php';

// check for required fields
if (isset($_POST['googleID']) && isset($_POST['firstName']) && isset($_POST['lastName']) && isset($_POST['email']) && strlen($_POST['googleID']) == 21 && is_numeric($_POST['googleID']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    $googleID = mysql_real_escape_string($_POST['googleID']);
	$googleID_1 = substr($googleID,0,10);
	$googleID_2 = substr($googleID,10,11);
    $firstName = mysql_real_escape_string($_POST['firstName']);
	$lastName = mysql_real_escape_string($_POST['lastName']);
	$email = mysql_real_escape_string($_POST['email']);
 
    $duplicatesql = "SELECT * FROM userprofiles where (googleID_1 = '$googleID_1' AND googleID_2 = '$googleID_2')";
	$duplicates = mysql_query($duplicatesql);
	//var_dump(mysql_num_rows($duplicates));

	if (mysql_num_rows($duplicates) == false) {
		
		// mysql inserting a new row
		if (isset($_POST['pictureURL'])){
			$pictureURL = mysql_real_escape_string($_POST['pictureURL']);
			$result = mysql_query("INSERT INTO userprofiles(googleID_1, googleID_2,firstName, lastName, email, pictureURL, createdAt, updatedAt) VALUES('$googleID_1', '$googleID_2', '$firstName', '$lastName', '$email', '$pictureURL',now(),now())");
		}
		 else {
			$result = mysql_query("INSERT INTO userprofiles(googleID_1, googleID_2,firstName, lastName, email, createdAt, updatedAt) VALUES('$googleID_1', '$googleID_2', '$firstName', '$lastName', '$email',now(),now())");
		}
	 
		// check if row inserted or not
		if ($result) {
			// successfully inserted into database
			$response["success"] = 1;
			$response["message"] = "User profile with id ".$googleID_1.$googleID_2." successfully created.";
			$response["userID"] =  mysql_insert_id();
	 
			// echoing JSON response
			echo json_encode($response);
		} else {
			// failed to insert row
			$response["success"] = 0;
			$response["message"] = "Oops! An error occurred.";
	 
			// echoing JSON response
			echo json_encode($response);
		}
	}
	else {
		$result = mysql_query("SELECT userID FROM userprofiles WHERE (googleID_1 = '$googleID_1' AND googleID_2 = '$googleID_2')");
		$result = mysql_fetch_array($result);
		$response["success"] = 0;
		$response["message"] = "Userprofile already in database with id".$result["userID"].".";
		$response["userID"] = $result["userID"];
	 
			// echoing JSON response
			echo json_encode($response);
	}
} 

else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>