$(document).ready(function() {


	$('#username').on('input', function() {
    const username = $(this);
    if (validateUsername(username.val())) {
      username.removeClass('is-invalid').addClass('is-valid');
    } else {
      username.removeClass('is-valid').addClass('is-invalid');
    }
  });

  $('#password').on('input', function() {
    const password = $(this);
    if (validatePassword(password.val())) {
      password.removeClass('is-invalid').addClass('is-valid');
    } else {
      password.removeClass('is-valid').addClass('is-invalid');
    }
  });

  const validateUsername = (username) =>
    /^([a-zA-Z0-9_-]{3,20})$/.test(username);
  const validatePassword = (password) =>
    /^(?=.*[0-9])(?=.*[a-zA-Z])\S{6,25}$/.test(password);


  $('.login').submit(function(event) {
    const form = $(this)[0];
    console.log($(this).find('.is-invalid'));
    if ($(this).find('.is-invalid').length > 0) {
      event.preventDefault();
      event.stopPropagation();
    }
  });

  $('.needs-validation').submit(function(event) {
    const form = $(this)[0];
    if (form.checkValidity() === false) {
      event.preventDefault();
      event.stopPropagation();
    }
    form.classList.add('was-validated');
  });

  $('.range').on('input', function(event) {
    if (Number(this.value) > this.max) {
      this.value = this.max;
    } else if (Number(this.value) < this.min) {
      this.value = this.min;
    }
  });

});



