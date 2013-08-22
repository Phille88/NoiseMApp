<?php
 
// array for JSON response
$response = array();
 
require_once '/home/thesis-std/request_key.php';

if (isset($_POST['userID']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
	// include db connect class
	require_once __DIR__ . '/db_connect.php';
	 
	// connecting to db
	$db = new DB_CONNECT();

    $userID = mysql_real_escape_string($_POST['userID']);
	
	// get all products from products table
	$result = mysql_query("SELECT up.*, tp.totalPoints
FROM userprofiles up 
JOIN friendships fs ON fs.userID2 = up.userID
LEFT JOIN totalpoints tp ON tp.userID = up.userID
WHERE fs.userID1 = $userID
GROUP BY up.userID") or die(mysql_error());
	 
	if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {	
			$response["friends"] = array();
			
			while($row = mysql_fetch_array($result)){
				$friend = array();
				$friend["userID"] = $row["userID"];
				$friend["googleID"] = $row["googleID_1"] . $row["googleID_2"];
				$friend["firstName"] = $row["firstName"];
				$friend["lastName"] = $row["lastName"];
				$friend["pictureURL"] = $row["pictureURL"];
				if($row["totalPoints"] == null)
					$row["totalPoints"] = "0";
				$friend["totalPoints"] = $row["totalPoints"];
				
				$oppUserID = $friend["userID"];
				$amountSBWon = mysql_query("SELECT COUNT(soundBattleID) FROM (SELECT sbtp.soundBattleID, sbtp.pointsUserID1 AS myPoints, sbtp.pointsUserID2 AS oppPoints FROM soundbattletotalpoints sbtp JOIN soundbattles sb ON sb.soundBattleID = sbtp.soundBattleID WHERE sb.userID1 = $oppUserID UNION SELECT sbtp.soundBattleID, sbtp.pointsUserID2 AS myPoints, sbtp.pointsUserID1 AS oppPoints FROM soundbattletotalpoints sbtp JOIN soundbattles sb ON sb.soundBattleID = sbtp.soundBattleID WHERE sb.userID2 = $oppUserID) AS sbpoints WHERE sbpoints.myPoints > sbpoints.oppPoints");
				
				if(!empty($amountSBWon)){
					$amountSBWon = mysql_fetch_array($amountSBWon);
					$friend["amountSBWon"] = $amountSBWon["COUNT(soundBattleID)"];
				}
				
				$badgeIDs = mysql_query("SELECT badgeID FROM badges WHERE userID = $oppUserID");
				$friend["badges"] = array();
				if (!empty($badgeIDs)) {
				if (mysql_num_rows($badgeIDs) > 0) {
					while($row = mysql_fetch_array($badgeIDs)){
						array_push($friend["badges"], $row["badgeID"]);
					}
				}
					
				$lastSoundCheckin_result = mysql_query("SELECT scr.placeName, nr.dB FROM soundcheckinrecordings scr JOIN noiserecordings nr ON nr.noiseID = scr.noiseRecordingID WHERE scr.userID = $oppUserID ORDER BY createdAt DESC LIMIT 0,1");
				if(!empty($lastSoundCheckin_result)){
					$lastSoundCheckin_result = mysql_fetch_array($lastSoundCheckin_result);
					$lastSoundCheckin["placeName"] = $lastSoundCheckin_result["placeName"];
					if($lastSoundCheckin_result["dB"] == null)
						$lastSoundCheckin_result["dB"] = 0;
					$lastSoundCheckin["dB"] = $lastSoundCheckin_result["dB"];
					$friend["lastSoundCheckin"] = $lastSoundCheckin;
				}
			}
				
				
				array_push($response["friends"],$friend);
			}
			// success
			$response["success"] = 1;
		 
			// echoing JSON response
			echo json_encode($response);
		} else {
			// no products found
			$response["success"] = 0;
			$response["message"] = "No friends found";
		 
			// echo no users JSON
			echo json_encode($response);
		}
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>