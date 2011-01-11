<?php
	if (isset($_GET['target'])) {
		switch ($_GET['target']) {
			case "FAQs":
				redirect("http://logsaw.sourceforge.net/?page_id=70&from_app=true");
				break;
			case "HelpForum":
				redirect("http://sourceforge.net/projects/logsaw/forums/forum/1097019");
				break;
			case "Homepage":
				redirect("http://logsaw.sourceforge.net/?from_app=true");
				break;
			default:
				redirect("http://logsaw.sourceforge.net/");
		}
	} else {
		redirect("http://logsaw.sourceforge.net/");
	}
	
	function redirect($url) {
		header("Location: " . $url);
	}
?>
