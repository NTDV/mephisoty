export class DateTimeService {
    getDateNow() {
        return new Date();
    }

    formatDateTimeFromDate(date) {
        const year = date.getFullYear(),
            month = date.getMonth() + 1, // months are zero indexed
            monthFormatted = month < 10 ? '0' + month : month,
            day = date.getDate(),
            dayFormatted = day < 10 ? '0' + day : day,
            hour = date.getHours(),
            minute = date.getMinutes(),
            second = date.getSeconds(),
            secondFormatted = second < 10 ? '0' + second : second,
            _hourFormatted = hour, // hour returned in 24 hour format
            hourFormatted = _hourFormatted < 10 ? '0' + _hourFormatted : _hourFormatted,
            minuteFormatted = minute < 10 ? '0' + minute : minute;

        return dayFormatted + '.' + monthFormatted + '.' + year + ' ' + hourFormatted + ':' + minuteFormatted + ':' + secondFormatted;
    }

    getDateFromTimestamp(timestamp) {
        return new Date(timestamp * 1000);
    }

    getDateTimeOffsetFromDate(date) {
        return date.toISOString();
    }
}
