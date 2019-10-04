const USER_URL = buildUrlWithContextPath("user");
var numberOfRepositories = 0;


function extractLatestCommit(commits) {
    var commitsArray = [];
    $.each( commits, function (key, value){
        commitsArray.push({
            "key" : key,
            "value" : value
            })
    });

    commitsArray.sort(function(a,b){
        var c = new Date(a.value.m_CommitDate);
        var d = new Date(b.value.m_CommitDate);
        return d-c;
    });

    return commitsArray[0];

}

function extractNumberOfRepositoryBranches(branches) {
    var branchesCounter = 0;

    $.each( branches, function (key, value){
        branchesCounter++;
    });

    return branchesCounter;
}

$(function() { // onload function- load repositories cards
    console.log("check2");

    $.ajax({
        url: "/magitHub/pages/main/user",
        data:{"isLoggedInUser" : "TRUE"},
        //timeout: 2000, TODO delete comment
        error: function() {
            console.log("no");
        },
        success: function(data) {
            console.log(data);
            var repositoriesArray = data.m_Engine.m_Repositories;

            $.each( repositoriesArray, function (key, value){
                if (numberOfRepositories % 3 === 0){
                    var rowElem = $('<div class="row">');
                    $( ".row.mb-4").before(rowElem);
                }

                var repositoryName = value.m_Name;
                var magitObj = value.m_Magit;
                var repositoryActiveBranchName = "Active branch : " + magitObj.m_Head.m_ActiveBranch.m_Name + '<br>';
                var numberOfRepositoryBranches = "Branches amount : " + extractNumberOfRepositoryBranches(magitObj.m_Branches) + '<br>';
                var latestCommit = extractLatestCommit(magitObj.m_Commits);
                var latestCommitDate = "Latest commit date : " + latestCommit.value.m_CommitDate + '<br>';
                var latestCommitMessage = "Latest commit message : " +  latestCommit.value.m_Message + '<br>';

                var commitDetails = repositoryActiveBranchName + numberOfRepositoryBranches + latestCommitDate + latestCommitMessage;

                $(".row").eq(-2).append( $('<div class="col-lg-4 mb-4">'));
                $(".col-lg-4:last").append( $('<div class="card h-100">'));
                $(".card.h-100:last").append($('<h4 class="card-header" id="repositoryName" value=""' + repositoryName + '>' + repositoryName + '</h4>'));
                $(".card.h-100:last").append($('<div class="card-body">'));
                $(".card-body:last").append( $('<p class="card-text">' + commitDetails + '</p>'));
                $(".card.h-100:last").append($('<div class="card-footer">'));
                $(".card-footer:last").append( $('<a href="#" class="btn btn-primary" id="chooseRepo">Choose repository</a>'));
                $("#chooseRepo").click(function () {
                    var url = "../repository/repository.html?repositoryName=" + repositoryName + "&userName=" + data.m_Name;
                    window.location.href = url;
                });


                numberOfRepositories++;
            });
        }
    });
});

$(function() { // onload function- attach functionality to upload repository button
    $("#uploadRepositoryForm").submit(function() {

        var file1 = this[0].files[0];

        var formData = new FormData();
        formData.append("fake-key-1", file1);

        $.ajax({
            method:'POST',
            data: formData,
            url: this.action,
            processData: false, // Don't process the files
            contentType: false, // Set content type to false as jQuery will tell the server its a query string request
            //timeout: 4000, TODO delete comment
            error: function(xhr, status, error) {
                alert("Repository upload failed: " + xhr.responseText);
            },
            success: function(r) {
                location.reload();
                alert("a new repository loaded successfully")
            }
        });

        // return value of the submit operation
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    })
});

$(function() { // onload function- update users list in side bar

    $.ajax({
        method:'get',
        url: "/magitHub/pages/main/usersList",
        data: {"onlyActiveUsers" : "TRUE"},
        //timeout: 4000, TODO delete comment
        error: function(e) {
            //alert("Unable to load users list in side bar")
        },
        success: function(data) {
            // data represent an array of users that have at least 1 repository

            for (var i = 0; i < data.length ; i++){
                $(".side-bar").append($('<li class="list-group-item pl-3 py-2 user-item">'));
                $(".user-item:last").append($('<a href="#"><i class="fa fa-user-o" aria-hidden="true"><span class="ml-2 align-middle">' + data[i] + '</span></i></a>'));
            }
        }
    });
});