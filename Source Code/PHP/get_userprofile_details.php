<?php 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';
 
// check for post data
if (isset($_POST['userID']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
    $userID = mysql_real_escape_string($_POST['userID']);
 
    // get a userprofile from userprofiles table
    $result = mysql_query("SELECT up.*, tp.totalPoints
FROM userprofiles up 
LEFT JOIN totalpoints tp ON tp.userID = up.userID
WHERE up.userID = $userID");

	$amountSBWon = mysql_query("SELECT COUNT(soundBattleID) FROM (SELECT sbtp.soundBattleID, sbtp.pointsUserID1 AS myPoints, sbtp.pointsUserID2 AS oppPoints FROM soundbattletotalpoints sbtp JOIN soundbattles sb ON sb.soundBattleID = sbtp.soundBattleID WHERE sb.userID1 = $userID UNION SELECT sbtp.soundBattleID, sbtp.pointsUserID2 AS myPoints, sbtp.pointsUserID1 AS oppPoints FROM soundbattletotalpoints sbtp JOIN soundbattles sb ON sb.soundBattleID = sbtp.soundBattleID WHERE sb.userID2 = $userID) AS sbpoints WHERE sbpoints.myPoints > sbpoints.oppPoints");
 
    if (!empty($result)) {
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            //$userprofile = array();
			$userprofile["userID"] = $result["userID"];
            $userprofile["googleID"] = $result["googleID_1"].$result["googleID_2"];
            $userprofile["firstName"] = $result["firstName"];
            $userprofile["lastName"] = $result["lastName"];
            $userprofile["email"] = $result["email"];
			$userprofile["pictureURL"] = $result["pictureURL"];
            $userprofile["createdAt"] = $result["createdAt"];
            $userprofile["updatedAt"] = $result["updatedAt"];
			if($result["totalPoints"] == null)
					$result["totalPoints"] = "0";
			$userprofile["totalPoints"] = $result["totalPoints"];
			
			if(!empty($amountSBWon)){
				$amountSBWon = mysql_fetch_array($amountSBWon);
				$userprofile["amountSBWon"] = $amountSBWon["COUNT(soundBattleID)"];
			}
			
			
			$badgeIDs = mysql_query("SELECT badgeID FROM badges WHERE userID = $userID");
			$userprofile["badges"] = array();
			if (!empty($badgeIDs)) {
				if (mysql_num_rows($badgeIDs) > 0) {
					while($row = mysql_fetch_array($badgeIDs)){
						array_push($userprofile["badges"], $row["badgeID"]);
					}
				}
			}
			
			$lastSoundCheckin_result = mysql_query("SELECT scr.placeName, nr.dB FROM soundcheckinrecordings scr JOIN noiserecordings nr ON nr.noiseID = scr.noiseRecordingID WHERE scr.userID = $userID ORDER BY createdAt DESC LIMIT 0,1");
			if(!empty($lastSoundCheckin_result)){
				$lastSoundCheckin_result = mysql_fetch_array($lastSoundCheckin_result);
				$lastSoundCheckin["placeName"] = $lastSoundCheckin_result["placeName"];
				if($lastSoundCheckin_result["dB"] == null)
					$lastSoundCheckin_result["dB"] = 0;
				$lastSoundCheckin["dB"] = $lastSoundCheckin_result["dB"];
				$userprofile["lastSoundCheckin"] = $lastSoundCheckin;
			}
            // success
            $response["success"] = 1;
 
            $response["userprofile"] = $userprofile;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no userprofile found
            $response["success"] = 0;
            $response["message"] = "No user profile found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no userprofile found
        $response["success"] = 0;
        $response["message"] = "No user profile found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>