export class DateTimeService {
  getDateNow() {
    return new Date();
  }

  getOnlyDayAndMonth(dateTime) {
    const month = dateTime.getMonth() + 1, // months are zero indexed
      monthFormatted = month < 10 ? '0' + month : month,
      day = dateTime.getDate(),
      dayFormatted = day < 10 ? '0' + day : day;

    return dayFormatted + '.' + monthFormatted;
  }

  getOnlyDate(dateTime) {
    const
      year = dateTime.getFullYear(),
      month = dateTime.getMonth() + 1, // months are zero indexed
      monthFormatted = month < 10 ? '0' + month : month,
      day = dateTime.getDate(),
      dayFormatted = day < 10 ? '0' + day : day;

    return dayFormatted + '.' + monthFormatted + '.' + year;
  }

  getOnlyHoursMinutes(dateTime) {
    const hour = dateTime.getHours(),
      minute = dateTime.getMinutes(),
      hourFormatted = hour < 10 ? '0' + hour : hour,
      minuteFormatted = minute < 10 ? '0' + minute : minute;

    return hourFormatted + ':' + minuteFormatted;
  }

  getOnlyTime(dateTime) {
    const
      hour = dateTime.getHours(),
      minute = dateTime.getMinutes(),
      hourFormatted = hour < 10 ? '0' + hour : hour,
      minuteFormatted = minute < 10 ? '0' + minute : minute,
      second = dateTime.getSeconds(),
      secondFormatted = second < 10 ? '0' + second : second
    ;

    return hourFormatted + ':' + minuteFormatted + ':' + secondFormatted;
  }

  formatDateTimeFromDate(dateTime) {
    const
      year = dateTime.getFullYear(),
      month = dateTime.getMonth() + 1, // months are zero indexed
      monthFormatted = month < 10 ? '0' + month : month,
      day = dateTime.getDate(),
      dayFormatted = day < 10 ? '0' + day : day,

      hour = dateTime.getHours(),
      minute = dateTime.getMinutes(),
      hourFormatted = hour < 10 ? '0' + hour : hour,
      minuteFormatted = minute < 10 ? '0' + minute : minute,
      second = dateTime.getSeconds(),
      secondFormatted = second < 10 ? '0' + second : second
    ;

    return dayFormatted + '.' + monthFormatted + '.' + year + ' ' + hourFormatted + ':' + minuteFormatted + ':' + secondFormatted;
  }

  getDateFromTimestamp(timestamp) {
    return new Date(timestamp * 1000);
  }

  getDateTimeOffsetFromDate(date) {
    return date.toISOString();
  }

  translateLocalDateToDayMonth(localDateString) {
    const split = localDateString.split('-'),
      day = split[2],
      month = split[1];

    return day + '.' + month;
  }
}
