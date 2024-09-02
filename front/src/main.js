import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

import PrimeVue from 'primevue/config';
import AutoComplete from 'primevue/autocomplete';
import Accordion from 'primevue/accordion';
import AccordionTab from 'primevue/accordiontab';
import Avatar from 'primevue/avatar';
import AvatarGroup from 'primevue/avatargroup';
import Badge from 'primevue/badge';
import BadgeDirective from 'primevue/badgedirective';
import BlockUI from 'primevue/blockui';
import Button from 'primevue/button';
import ButtonGroup from 'primevue/buttongroup';
import Breadcrumb from 'primevue/breadcrumb';
import Calendar from 'primevue/calendar';
import Card from 'primevue/card';
import Chart from 'primevue/chart';
import CascadeSelect from 'primevue/cascadeselect';
import Carousel from 'primevue/carousel';
import Checkbox from 'primevue/checkbox';
import Chip from 'primevue/chip';
import Chips from 'primevue/chips';
import ColorPicker from 'primevue/colorpicker';
import Column from 'primevue/column';
import ColumnGroup from 'primevue/columngroup';
import ConfirmDialog from 'primevue/confirmdialog';
import ConfirmPopup from 'primevue/confirmpopup';
import ConfirmationService from 'primevue/confirmationservice';
import ContextMenu from 'primevue/contextmenu';
import DataTable from 'primevue/datatable';
import DataView from 'primevue/dataview';
import DataViewLayoutOptions from 'primevue/dataviewlayoutoptions';
import DeferredContent from 'primevue/deferredcontent';
import Dialog from 'primevue/dialog';
import DialogService from 'primevue/dialogservice';
import Divider from 'primevue/divider';
import Dock from 'primevue/dock';
import Dropdown from 'primevue/dropdown';
import DynamicDialog from 'primevue/dynamicdialog';
import Fieldset from 'primevue/fieldset';
import FileUpload from 'primevue/fileupload';
import FloatLabel from 'primevue/floatlabel';
import Galleria from 'primevue/galleria';
import IconField from 'primevue/iconfield';
import Image from 'primevue/image';
import InlineMessage from 'primevue/inlinemessage';
import Inplace from 'primevue/inplace';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import InputIcon from 'primevue/inputicon';
import InputSwitch from 'primevue/inputswitch';
import InputText from 'primevue/inputtext';
import InputMask from 'primevue/inputmask';
import InputNumber from 'primevue/inputnumber';
import Knob from 'primevue/knob';
import Listbox from 'primevue/listbox';
import MegaMenu from 'primevue/megamenu';
import Menu from 'primevue/menu';
import Menubar from 'primevue/menubar';
import Message from 'primevue/message';
import MultiSelect from 'primevue/multiselect';
import OrderList from 'primevue/orderlist';
import OrganizationChart from 'primevue/organizationchart';
import OverlayPanel from 'primevue/overlaypanel';
import Paginator from 'primevue/paginator';
import Panel from 'primevue/panel';
import PanelMenu from 'primevue/panelmenu';
import Password from 'primevue/password';
import PickList from 'primevue/picklist';
import ProgressBar from 'primevue/progressbar';
import ProgressSpinner from 'primevue/progressspinner';
import Rating from 'primevue/rating';
import RadioButton from 'primevue/radiobutton';
import Ripple from 'primevue/ripple';
import Row from 'primevue/row';
import SelectButton from 'primevue/selectbutton';
import ScrollPanel from 'primevue/scrollpanel';
import ScrollTop from 'primevue/scrolltop';
import Skeleton from 'primevue/skeleton';
import Slider from 'primevue/slider';
import Sidebar from 'primevue/sidebar';
import SpeedDial from 'primevue/speeddial';
import SplitButton from 'primevue/splitbutton';
import Splitter from 'primevue/splitter';
import SplitterPanel from 'primevue/splitterpanel';
import Steps from 'primevue/steps';
import StyleClass from 'primevue/styleclass';
import TabMenu from 'primevue/tabmenu';
import TieredMenu from 'primevue/tieredmenu';
import Textarea from 'primevue/textarea';
import Toast from 'primevue/toast';
import ToastService from 'primevue/toastservice';
import Toolbar from 'primevue/toolbar';
import TabView from 'primevue/tabview';
import TabPanel from 'primevue/tabpanel';
import Tag from 'primevue/tag';
import Terminal from 'primevue/terminal';
import Timeline from 'primevue/timeline';
import ToggleButton from 'primevue/togglebutton';
import Tooltip from 'primevue/tooltip';
import Tree from 'primevue/tree';
import TreeSelect from 'primevue/treeselect';
import TreeTable from 'primevue/treetable';
import TriStateCheckbox from 'primevue/tristatecheckbox';
import VirtualScroller from 'primevue/virtualscroller';

import BlockViewer from '@/components/BlockViewer.vue';

import '@/assets/styles.scss';
import CalendarInputBlock from '@/components/prefab/CalendarInputBlock.vue';
import CreatedModifiedBlock from '@/components/prefab/CreatedModifiedBlock.vue';
import TextareaBlock from '@/components/prefab/TextareaBlock.vue';
import TextInputBlock from '@/components/prefab/TextInputBlock.vue';
import ViewStateInputBlock from '@/components/prefab/ViewStateInputBlock.vue';
import InputNumberBlock from '@/components/prefab/InputNumberBlock.vue';
import SkeletonAdminView from '@/components/prefab/SkeletonAdminView.vue';
import UserNameIdBlock from '@/components/prefab/UserNameIdBlock.vue';
import ScoreEditorBlock from '@/components/prefab/ScoreEditorBlock.vue';
import axios from 'axios';
import { FilterService } from 'primevue/api';
import AchievementTypeInputBlock from '@/components/prefab/AchievementTypeInputBlock.vue';
import { getPayload } from '@/service/util/UtilsService';
import AppTitle from '@/components/prefab/AppTitle.vue';

import Particles from '@tsparticles/vue3';
import { loadSlim } from '@tsparticles/slim';
import TextRunner from '@/components/prefab/TextRunner.vue';
import StagePublicBlock from '@/components/prefab/StagePublicBlock.vue';
import FilesEditor from '@/components/prefab/FilesEditor.vue';

const app = createApp(App);
const locale = await fetch('/locale/ru.json')
  .then((res) => res.json())
  .then((d) => d.ru);

router.beforeEach(async (to) => {
  const path = to.path;
  const role = localStorage.role;

  if (path.startsWith('/admin')) {
    if (role !== 'ADMIN')
      return { name: 'accessDenied' };

  } else if (path.startsWith('/expert')) {
    if (role !== 'EXPERT' && role !== 'ADMIN')
      return { name: 'accessDenied' };

  } else if (path.startsWith('/participant')) {
    if (role !== 'PARTICIPANT' && role !== 'ADMIN')
      return { name: 'accessDenied' };
  }
});

app.use(router);
app.use(PrimeVue, { ripple: true, locale: locale });
app.use(ToastService);
app.use(DialogService);
app.use(ConfirmationService);
app.use(Particles, {
  init: async (engine) => {
    await loadSlim(engine);
  }
});

app.directive('tooltip', Tooltip);
app.directive('badge', BadgeDirective);
app.directive('ripple', Ripple);
app.directive('styleclass', StyleClass);

app.component('BlockViewer', BlockViewer);

app.component('Accordion', Accordion);
app.component('AccordionTab', AccordionTab);
app.component('AutoComplete', AutoComplete);
app.component('Avatar', Avatar);
app.component('AvatarGroup', AvatarGroup);
app.component('Badge', Badge);
app.component('BlockUI', BlockUI);
app.component('Breadcrumb', Breadcrumb);
app.component('Button', Button);
app.component('ButtonGroup', ButtonGroup);
app.component('Calendar', Calendar);
app.component('Card', Card);
app.component('Chart', Chart);
app.component('Carousel', Carousel);
app.component('CascadeSelect', CascadeSelect);
app.component('Checkbox', Checkbox);
app.component('Chip', Chip);
app.component('Chips', Chips);
app.component('ColorPicker', ColorPicker);
app.component('Column', Column);
app.component('ColumnGroup', ColumnGroup);
app.component('ConfirmDialog', ConfirmDialog);
app.component('ConfirmPopup', ConfirmPopup);
app.component('ContextMenu', ContextMenu);
app.component('DataTable', DataTable);
app.component('DataView', DataView);
app.component('DataViewLayoutOptions', DataViewLayoutOptions);
app.component('DeferredContent', DeferredContent);
app.component('Dialog', Dialog);
app.component('Divider', Divider);
app.component('Dock', Dock);
app.component('Dropdown', Dropdown);
app.component('DynamicDialog', DynamicDialog);
app.component('Fieldset', Fieldset);
app.component('FileUpload', FileUpload);
app.component('FloatLabel', FloatLabel);
app.component('Galleria', Galleria);
app.component('IconField', IconField);
app.component('Image', Image);
app.component('InlineMessage', InlineMessage);
app.component('Inplace', Inplace);
app.component('InputGroup', InputGroup);
app.component('InputGroupAddon', InputGroupAddon);
app.component('InputIcon', InputIcon);
app.component('InputMask', InputMask);
app.component('InputNumber', InputNumber);
app.component('InputSwitch', InputSwitch);
app.component('InputText', InputText);
app.component('Knob', Knob);
app.component('Listbox', Listbox);
app.component('MegaMenu', MegaMenu);
app.component('Menu', Menu);
app.component('Menubar', Menubar);
app.component('Message', Message);
app.component('MultiSelect', MultiSelect);
app.component('OrderList', OrderList);
app.component('OrganizationChart', OrganizationChart);
app.component('OverlayPanel', OverlayPanel);
app.component('Paginator', Paginator);
app.component('Panel', Panel);
app.component('PanelMenu', PanelMenu);
app.component('Password', Password);
app.component('PickList', PickList);
app.component('ProgressBar', ProgressBar);
app.component('ProgressSpinner', ProgressSpinner);
app.component('RadioButton', RadioButton);
app.component('Rating', Rating);
app.component('Row', Row);
app.component('SelectButton', SelectButton);
app.component('ScrollPanel', ScrollPanel);
app.component('ScrollTop', ScrollTop);
app.component('Slider', Slider);
app.component('Sidebar', Sidebar);
app.component('Skeleton', Skeleton);
app.component('SpeedDial', SpeedDial);
app.component('SplitButton', SplitButton);
app.component('Splitter', Splitter);
app.component('SplitterPanel', SplitterPanel);
app.component('Steps', Steps);
app.component('TabMenu', TabMenu);
app.component('TabView', TabView);
app.component('TabPanel', TabPanel);
app.component('Tag', Tag);
app.component('Textarea', Textarea);
app.component('Terminal', Terminal);
app.component('TieredMenu', TieredMenu);
app.component('Timeline', Timeline);
app.component('Toast', Toast);
app.component('Toolbar', Toolbar);
app.component('ToggleButton', ToggleButton);
app.component('Tree', Tree);
app.component('TreeSelect', TreeSelect);
app.component('TreeTable', TreeTable);
app.component('TriStateCheckbox', TriStateCheckbox);
app.component('VirtualScroller', VirtualScroller);

app.component('CalendarInputBlock', CalendarInputBlock);
app.component('CreatedModifiedBlock', CreatedModifiedBlock);
app.component('TextareaBlock', TextareaBlock);
app.component('TextInputBlock', TextInputBlock);
app.component('ViewStateInputBlock', ViewStateInputBlock);
app.component('InputNumberBlock', InputNumberBlock);
app.component('SkeletonAdminView', SkeletonAdminView);
app.component('UserNameIdBlock', UserNameIdBlock);
app.component('ScoreEditorBlock', ScoreEditorBlock);
app.component('AchievementTypeInputBlock', AchievementTypeInputBlock);
app.component('AppTitle', AppTitle);
app.component('TextRunner', TextRunner);
app.component('StagePublicBlock', StagePublicBlock);
app.component('FilesEditor', FilesEditor);

app.mount('#app');

app.config.globalProperties.window = window;

window.$apiHost = 'https://beststudents.mephi.ru/api';
window.$frontHost = 'https://beststudents.mephi.ru';
//window.$apiHost = 'http://100.97.110.74:80/api';
//window.$frontHost = 'http://100.97.110.74:5173';

FilterService.register('skip', (value, filter, filterLocale) => {
  return true;
});

function updateOptions(options) {
  const update = {
    ...options,
    follow: true
  };

  if (localStorage.jwt) {
    update.headers = {
      ...update.headers,
      Authorization: `Bearer ${localStorage.jwt}`
    };
  }

  update.headers = {
    ...update.headers,
    'Content-Type': 'application/json'
    //, 'Accept': 'application/json'
  };
  return update;
}

const urlParams = new URLSearchParams(window.location.search);
if (urlParams.has('ticket')) {
  const ticket = urlParams.get('ticket');
  fetch(window.$apiHost + '/auth/login?ticket=' + ticket)
    .then(res => res.json())
    .then(json => {
      if (json && json.token) {
        localStorage.jwt = json.token;
        localStorage.jwt_payload = getPayload(json.token);
        const creds = JSON.parse(localStorage.jwt_payload);

        localStorage.fio = `${creds.user.secondName} ${creds.user.firstName[0]}.${
          creds.user.thirdName && creds.user.thirdName !== '' ? ' ' + creds.user.thirdName[0] + '.' : ''}`;
        localStorage.fullName = `${creds.user.secondName} ${creds.user.firstName} ${creds.user.thirdName}`.trim();
        localStorage.isNew = creds.user.isNew;
        localStorage.role = creds.role;
        localStorage.cred_id = creds.id;

        if (creds.role === 'ADMIN') {
          window.location.replace(window.$frontHost + '/admin/seasons');
        } else if (creds.role === 'EXPERT') {
          window.location.replace(window.$frontHost + '/expert/me');
        } else if (creds.role === 'PARTICIPANT') {
          window.location.replace(window.$frontHost + '/participant/me');
        } else {
          throw new Error('Invalid role');
        }
      } else throw new Error('Invalid ticket');
    })
    .catch(e => {
      console.error(e);
      return router.push('/access');
    });
}

function getFullUrl(relativeUrl) {
  return window.$apiHost + (relativeUrl.startsWith('/') ? relativeUrl : '/' + relativeUrl);
}

export default function fetchApi(relativeUrl, options, isFile = false) {
  if (relativeUrl) {
    if (isFile) {
      return axios.post(getFullUrl(relativeUrl), options, {
        headers: {
          'Authorization': `Bearer ${localStorage.jwt}`,
          'Content-Type': 'multipart/form-data'
        }
      });
    } else {
      return fetch(getFullUrl(relativeUrl), updateOptions(options))
        .then(res => {
          if (res.status === 403) {
            window.location.href = 'https://auth.mephi.ru/login?service=' + encodeURI(window.$frontHost);
          } else if (res.status >= 400) {
            router.push('/error');
          } else {
            return res;
          }
        });
    }
  }
}