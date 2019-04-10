/**
 * jQuery Form Validator
 * ------------------------------------------
 *
 * Czech language package
 *
 * @website http://formvalidator.net/
 * @license MIT
 */
(function($, window) {

    'use strict';

    $(window).bind('validatorsLoaded', function() {

        $.formUtils.LANG = {
            errorTitle: '提交失败!',
            requiredField: '必填字段',
            requiredFields: '没有填完整必填字段',
            badTime: 'You have not given a correct time',
            badEmail: 'You have not given a correct e-mail address',
            badTelephone: 'You have not given a correct phone number',
            badSecurityAnswer: 'You have not given a correct answer to the security question',
            badDate: 'You have not given a correct date',
            lengthBadStart: 'The input value must be between ',
            lengthBadEnd: ' characters',
            lengthTooLongStart: 'The input value is longer than ',
            lengthTooShortStart: 'The input value is shorter than ',
            notConfirmed: 'Input values could not be confirmed',
            badDomain: 'Incorrect domain value',
            badUrl: 'The input value is not a correct URL',
            badCustomVal: 'The input value is incorrect',
            andSpaces: ' and spaces ',
            badInt: '请输入数字',
            badSecurityNumber: 'Your social security number was incorrect',
            badUKVatAnswer: 'Incorrect UK VAT Number',
            badStrength: 'The password isn\'t strong enough',
            badNumberOfSelectedOptionsStart: 'You have to choose at least ',
            badNumberOfSelectedOptionsEnd: ' answers',
            badAlphaNumeric: 'The input value can only contain alphanumeric characters ',
            badAlphaNumericExtra: ' and ',
            wrongFileSize: 'The file you are trying to upload is too large (max %s)',
            wrongFileType: 'Only files of type %s is allowed',
            groupCheckedRangeStart: 'Please choose between ',
            groupCheckedTooFewStart: 'Please choose at least ',
            groupCheckedTooManyStart: 'Please choose a maximum of ',
            groupCheckedEnd: ' item(s)',
            badCreditCard: 'The credit card number is not correct',
            badCVV: 'The CVV number was not correct',
            wrongFileDim : 'Incorrect image dimensions,',
            imageTooTall : 'the image can not be taller than',
            imageTooWide : 'the image can not be wider than',
            imageTooSmall : 'the image was too small',
            min : 'min',
            max : 'max',
            imageRatioNotAccepted : 'Image ratio is not be accepted',
            badBrazilTelephoneAnswer: 'The phone number entered is invalid',
            badBrazilCEPAnswer: 'The CEP entered is invalid',
            badBrazilCPFAnswer: 'The CPF entered is invalid'
        };

    });

})(jQuery, window);