// JavaScript Document
$( document ).ready(function() {
  "use strict";    

    // Nav Menu for Menumarker
    $("#navmenu").menumaker({
        title: "菜单",
        format: "multitoggle"
    });      

    // Main Slider
    $('.slider').owlCarousel({
      loop: true,
      items: 1,
      dots: false,
      nav: false,
      autoHeight: false,
      touchDrag: true,
      mouseDrag: true,
      margin: 0,
      autoplay: true,
      slideSpeed: 300,
      navText: ['', ''],  
      responsive: {
        0: {
            items: 1,
            nav: false,
            dots: false,
        },
        600: {
            items: 1,
            nav: false,
            dots: false,
        },
        768: {
            items: 1,
            nav: false,
        },
        1100: {
            items: 1,
            nav: false,
        }
      }
    })

    // Testimonial Box 1 Column
    $('#testimonial-box1').owlCarousel({
    loop:true,
    margin:0,
    autoplay:true,
    autoplayTimeout:3000,
    nav:false,
    dots:true,
    responsive:{
        0:{
            items:1
        },
        600:{
            items:1
        },
        1000:{
            items:1
        }
    }
    })

    // Testimonial Box 2 Column
    $('#testimonial-box2').owlCarousel({
    loop:true,
    margin:0,
    autoplay:true,
    autoplayTimeout:3000,
    nav:false,
    dots:true,
    responsive:{
        0:{
            items:1
        },
        600:{
            items:2
        },
        1000:{
            items:2
        }
    }
    })   

    // Career Slider
    $('#career-slider').owlCarousel({
    loop:true,
    margin:0,
    autoplay:true,
    autoplayTimeout:3000,
    nav:true,
    navText: ["<i class='fa fa-angle-left'></i>", "<i class='fa fa-angle-right'></i>"],    
    dots:false,
    responsive:{
        0:{
            items:1
        },
        600:{
            items:1
        },
        1000:{
            items:1
        }
    }
    })       

    // Video Play     
    $('.btn-video-play').on('click',function() {
      $('.video-item .video-preview').append(video_url);
      $(this).hide();
    }); 

    // Facts Count 
    if (jQuery('.counter').length) {   
        jQuery('.counter').counterUp({
          delay: 20,
          time: 2000
      });  
    }    
    
    // Datepicker
    if ($('.date-pick').length) {
      $('.date-pick').datepicker({
        format : "dd/mm/yyyy",
        minDate: 1 
      });
    }

    // Video 1 play  
    $('#video1').on('click',function() {
      $('#video-box1 .video-preview').append(video_url);
      $(this).hide();
    });     

    // Video 2 play
    $('#video2').on('click',function() {
      $('#video-box2 .video-preview').append(video_url);
      $(this).hide();
    });   


    // Home 1 testimonial owl Lslider 
    var sync1 = $("#testimonial-content");
	var sync2 = $("#testimonial-thumb");
	
    var syncedSecondary = true;

    sync1.owlCarousel({
        items:1,
        loop:true,
        margin:10,
        autoplay:true,
        autoplayTimeout:6000,
        autoplayHoverPause:false,
        nav: false,
        dots: false
    }).on('changed.owl.carousel', syncPosition);

    sync2.on('initialized.owl.carousel', function () {
        sync2.find(".owl-item .projectitem").eq(0).addClass("active");
    })
    .owlCarousel({
        items:3,
        margin:0,
        autoplay:false,
        nav: false,
        dots: false
    }).on('changed.owl.carousel', syncPosition2);

function syncPosition(el) {
    //if you set loop to false, you have to restore this next line
    //var current = el.item.index;

    //if you disable loop you have to comment this block
    var count = el.item.count-1;
    var current = Math.round(el.item.index - (el.item.count/2) - .5);
    if(current < 0) {
        current = count;
    }
    if(current > count) {
        current = 0;
    }
    //end block

    sync2
    .find(".projectitem")
    .removeClass("active")
    .eq(current)
    .addClass("active");
}

function syncPosition2(el) {
    if(syncedSecondary) {
        var number = el.item.index;
        sync1.data('owl.carousel').to(number, 100, true);
    }
}

sync2.on("click", ".projectitem", function(e){
    e.preventDefault();
    sync1.trigger('to.owl.carousel', [$(e.target).parents('.owl-item').index(), 300, true]);
});
// END Home 1 testimonial owl Lslider 
});


