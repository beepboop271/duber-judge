$(document).ready(function() {


	$('#username').on('input', function() {
    const username = $(this);
    if (validateUsername(username.val())) {
      username.removeClass('invalid').addClass('valid');
    } else {
      username.removeClass('valid').addClass('invalid');
    }
  });

  $('#password').on('input', function() {
    const password = $(this);
    if (validatePassword(password.val())) {
      password.removeClass('invalid').addClass('valid');
    } else {
      password.removeClass('valid').addClass('invalid');
    }
  });

  const validateUsername = (username) =>
    /^([a-zA-Z0-9_-]{3,20})$/.test(username);
  const validatePassword = (password) =>
    /^(?=.*[0-9])(?=.*[a-zA-Z])\S{6,25}$/.test(password);


  //login validation
  $('#login-form button').click(function(event){
    const data = $('#login-form').serializeArray();
    const validated = validateUsername(data[0].value) && validatePassword(data[1].value);
    const errorMsg = $('#error-msg');
    if (!validated) {
      errorMsg.removeClass('error').addClass('error-show');
      event.preventDefault();
    } else {
      errorMsg.removeClass('error-show').addClass('error');
    }
  });




});