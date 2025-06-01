/**
 * 
 */
console.log("This is script file")

const toggleSidebar = () => {
    if($(".sidebar").is(":visible")){
        //We have to close
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }else{
        //We have to show
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
}

const search = () => {
    let query = $("#search-input").val();

    if (query == "") {
        $(".search-result").hide();
    } else {
        // Log the query to make sure it's correct
        console.log(query);

        // Construct the correct URL
        let url = `http://localhost:8080/search/${query}`;

        // Sending request to server
        fetch(url)
            .then((response) => response.json())
            .then((data) => {
                // console.log(data);

				let text = `<div class='list-group'>`;
				
				data.forEach((contact=>{
					text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>` 
				}));
				
				text+=`</div>`;
				
				$(".search-result").html(text);
				$(".search-result").show();
				
                // Handle empty or null results
                if (data.length === 0) {
                    console.log("No results found.");
                }

                // Display search results
                $(".search-result").show();
                // You can add logic to display `data` on the page
            })
            .catch((error) => {
                console.error("Error fetching search results:", error);
            });
    }
};

