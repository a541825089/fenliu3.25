<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="链接url" prop="linkUrl">
        <el-input
          v-model="queryParams.linkUrl"
          placeholder="请输入链接url"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="(dict, index) in dict.type.sys_normal_disable"
            :key="dict.value + '_' + index"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:link:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:link:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:link:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:link:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="linkList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="链接ID" align="center" prop="linkId" />
      <el-table-column label="链接URL" align="center" prop="linkUrl" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-link type="primary" :href="scope.row.linkUrl" target="_blank">
            <i class="el-icon-link"></i> {{ scope.row.linkUrl }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="链接描述" align="center" prop="linkDescription" :show-overflow-tooltip="true" />
      <el-table-column label="回复语" align="center" prop="replyMsg" :show-overflow-tooltip="true" />
      <el-table-column label="随机打乱" align="center" prop="isScramble">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isScramble === '0' ? 'warning' : 'info'">
            {{ scope.row.isScramble === '0' ? '开启' : '关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:link:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:link:remove']"
          >删除</el-button>
          <el-dropdown size="mini" v-hasPermi="['system:link:query']">
            <el-button size="mini" type="text" icon="el-icon-d-arrow-right">更多</el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>详情</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改链接管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="投放国家" prop="targetCountry">
          <el-select v-model="form.targetCountry" placeholder="请选择投放国家" style="width: 100%" filterable clearable>
            <el-option label="请选择投放国家" value="" />
            <el-option
              v-for="(item, index) in countryOptions"
              :key="item.value + '_' + index"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分流链接" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="请输入分流链接">
            <el-button slot="append" icon="el-icon-edit"></el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="链接描述" prop="linkDescription">
          <el-input v-model="form.linkDescription" placeholder="请输入链接描述" />
        </el-form-item>
        <el-form-item label="IP防护" prop="ipProtection">
          <el-radio-group v-model="form.ipProtection">
            <el-radio label="0">开启</el-radio>
            <el-radio label="1">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="随机打乱" prop="isScramble">
          <el-radio-group v-model="form.isScramble">
            <el-radio label="0">开启</el-radio>
            <el-radio label="1">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listLink, getLink, delLink, addLink, updateLink } from "@/api/system/link";

const COUNTRY_OPTIONS = [
  { value: "AF", zh: "阿富汗", en: "Afghanistan" },
  { value: "AL", zh: "阿尔巴尼亚", en: "Albania" },
  { value: "DZ", zh: "阿尔及利亚", en: "Algeria" },
  { value: "AD", zh: "安道尔", en: "Andorra" },
  { value: "AO", zh: "安哥拉", en: "Angola" },
  { value: "AG", zh: "安提瓜和巴布达", en: "Antigua and Barbuda" },
  { value: "AR", zh: "阿根廷", en: "Argentina" },
  { value: "AM", zh: "亚美尼亚", en: "Armenia" },
  { value: "AU", zh: "澳大利亚", en: "Australia" },
  { value: "AT", zh: "奥地利", en: "Austria" },
  { value: "AZ", zh: "阿塞拜疆", en: "Azerbaijan" },
  { value: "BS", zh: "巴哈马", en: "Bahamas" },
  { value: "BH", zh: "巴林", en: "Bahrain" },
  { value: "BD", zh: "孟加拉国", en: "Bangladesh" },
  { value: "BB", zh: "巴巴多斯", en: "Barbados" },
  { value: "BY", zh: "白俄罗斯", en: "Belarus" },
  { value: "BE", zh: "比利时", en: "Belgium" },
  { value: "BZ", zh: "伯利兹", en: "Belize" },
  { value: "BJ", zh: "贝宁", en: "Benin" },
  { value: "BT", zh: "不丹", en: "Bhutan" },
  { value: "BO", zh: "玻利维亚", en: "Bolivia" },
  { value: "BA", zh: "波斯尼亚和黑塞哥维那", en: "Bosnia and Herzegovina" },
  { value: "BW", zh: "博茨瓦纳", en: "Botswana" },
  { value: "BR", zh: "巴西", en: "Brazil" },
  { value: "BN", zh: "文莱", en: "Brunei" },
  { value: "BG", zh: "保加利亚", en: "Bulgaria" },
  { value: "BF", zh: "布基纳法索", en: "Burkina Faso" },
  { value: "BI", zh: "布隆迪", en: "Burundi" },
  { value: "CV", zh: "佛得角", en: "Cabo Verde" },
  { value: "KH", zh: "柬埔寨", en: "Cambodia" },
  { value: "CM", zh: "喀麦隆", en: "Cameroon" },
  { value: "CA", zh: "加拿大", en: "Canada" },
  { value: "CF", zh: "中非共和国", en: "Central African Republic" },
  { value: "TD", zh: "乍得", en: "Chad" },
  { value: "CL", zh: "智利", en: "Chile" },
  { value: "CO", zh: "哥伦比亚", en: "Colombia" },
  { value: "KM", zh: "科摩罗", en: "Comoros" },
  { value: "CG", zh: "刚果共和国", en: "Republic of the Congo" },
  { value: "CD", zh: "刚果民主共和国", en: "Democratic Republic of the Congo" },
  { value: "CR", zh: "哥斯达黎加", en: "Costa Rica" },
  { value: "CI", zh: "科特迪瓦", en: "Cote d'Ivoire" },
  { value: "HR", zh: "克罗地亚", en: "Croatia" },
  { value: "CU", zh: "古巴", en: "Cuba" },
  { value: "CY", zh: "塞浦路斯", en: "Cyprus" },
  { value: "CZ", zh: "捷克", en: "Czechia" },
  { value: "DK", zh: "丹麦", en: "Denmark" },
  { value: "DJ", zh: "吉布提", en: "Djibouti" },
  { value: "DM", zh: "多米尼克", en: "Dominica" },
  { value: "DO", zh: "多米尼加", en: "Dominican Republic" },
  { value: "EC", zh: "厄瓜多尔", en: "Ecuador" },
  { value: "EG", zh: "埃及", en: "Egypt" },
  { value: "SV", zh: "萨尔瓦多", en: "El Salvador" },
  { value: "GQ", zh: "赤道几内亚", en: "Equatorial Guinea" },
  { value: "ER", zh: "厄立特里亚", en: "Eritrea" },
  { value: "EE", zh: "爱沙尼亚", en: "Estonia" },
  { value: "SZ", zh: "斯威士兰", en: "Eswatini" },
  { value: "ET", zh: "埃塞俄比亚", en: "Ethiopia" },
  { value: "FJ", zh: "斐济", en: "Fiji" },
  { value: "FI", zh: "芬兰", en: "Finland" },
  { value: "FR", zh: "法国", en: "France" },
  { value: "GA", zh: "加蓬", en: "Gabon" },
  { value: "GM", zh: "冈比亚", en: "Gambia" },
  { value: "GE", zh: "格鲁吉亚", en: "Georgia" },
  { value: "DE", zh: "德国", en: "Germany" },
  { value: "GH", zh: "加纳", en: "Ghana" },
  { value: "GR", zh: "希腊", en: "Greece" },
  { value: "GD", zh: "格林纳达", en: "Grenada" },
  { value: "GT", zh: "危地马拉", en: "Guatemala" },
  { value: "GN", zh: "几内亚", en: "Guinea" },
  { value: "GW", zh: "几内亚比绍", en: "Guinea-Bissau" },
  { value: "GY", zh: "圭亚那", en: "Guyana" },
  { value: "HT", zh: "海地", en: "Haiti" },
  { value: "HN", zh: "洪都拉斯", en: "Honduras" },
  { value: "HU", zh: "匈牙利", en: "Hungary" },
  { value: "IS", zh: "冰岛", en: "Iceland" },
  { value: "IN", zh: "印度", en: "India" },
  { value: "ID", zh: "印度尼西亚", en: "Indonesia" },
  { value: "IR", zh: "伊朗", en: "Iran" },
  { value: "IQ", zh: "伊拉克", en: "Iraq" },
  { value: "IE", zh: "爱尔兰", en: "Ireland" },
  { value: "IL", zh: "以色列", en: "Israel" },
  { value: "IT", zh: "意大利", en: "Italy" },
  { value: "JM", zh: "牙买加", en: "Jamaica" },
  { value: "JP", zh: "日本", en: "Japan" },
  { value: "JO", zh: "约旦", en: "Jordan" },
  { value: "KZ", zh: "哈萨克斯坦", en: "Kazakhstan" },
  { value: "KE", zh: "肯尼亚", en: "Kenya" },
  { value: "KI", zh: "基里巴斯", en: "Kiribati" },
  { value: "KP", zh: "朝鲜", en: "North Korea" },
  { value: "KR", zh: "韩国", en: "South Korea" },
  { value: "KW", zh: "科威特", en: "Kuwait" },
  { value: "KG", zh: "吉尔吉斯斯坦", en: "Kyrgyzstan" },
  { value: "LA", zh: "老挝", en: "Laos" },
  { value: "LV", zh: "拉脱维亚", en: "Latvia" },
  { value: "LB", zh: "黎巴嫩", en: "Lebanon" },
  { value: "LS", zh: "莱索托", en: "Lesotho" },
  { value: "LR", zh: "利比里亚", en: "Liberia" },
  { value: "LY", zh: "利比亚", en: "Libya" },
  { value: "LI", zh: "列支敦士登", en: "Liechtenstein" },
  { value: "LT", zh: "立陶宛", en: "Lithuania" },
  { value: "LU", zh: "卢森堡", en: "Luxembourg" },
  { value: "MG", zh: "马达加斯加", en: "Madagascar" },
  { value: "MW", zh: "马拉维", en: "Malawi" },
  { value: "MY", zh: "马来西亚", en: "Malaysia" },
  { value: "MV", zh: "马尔代夫", en: "Maldives" },
  { value: "ML", zh: "马里", en: "Mali" },
  { value: "MT", zh: "马耳他", en: "Malta" },
  { value: "MH", zh: "马绍尔群岛", en: "Marshall Islands" },
  { value: "MR", zh: "毛里塔尼亚", en: "Mauritania" },
  { value: "MU", zh: "毛里求斯", en: "Mauritius" },
  { value: "MX", zh: "墨西哥", en: "Mexico" },
  { value: "FM", zh: "密克罗尼西亚", en: "Micronesia" },
  { value: "MD", zh: "摩尔多瓦", en: "Moldova" },
  { value: "MC", zh: "摩纳哥", en: "Monaco" },
  { value: "MN", zh: "蒙古", en: "Mongolia" },
  { value: "ME", zh: "黑山", en: "Montenegro" },
  { value: "MA", zh: "摩洛哥", en: "Morocco" },
  { value: "MZ", zh: "莫桑比克", en: "Mozambique" },
  { value: "MM", zh: "缅甸", en: "Myanmar" },
  { value: "NA", zh: "纳米比亚", en: "Namibia" },
  { value: "NR", zh: "瑙鲁", en: "Nauru" },
  { value: "NP", zh: "尼泊尔", en: "Nepal" },
  { value: "NL", zh: "荷兰", en: "Netherlands" },
  { value: "NZ", zh: "新西兰", en: "New Zealand" },
  { value: "NI", zh: "尼加拉瓜", en: "Nicaragua" },
  { value: "NE", zh: "尼日尔", en: "Niger" },
  { value: "NG", zh: "尼日利亚", en: "Nigeria" },
  { value: "MK", zh: "北马其顿", en: "North Macedonia" },
  { value: "NO", zh: "挪威", en: "Norway" },
  { value: "OM", zh: "阿曼", en: "Oman" },
  { value: "PK", zh: "巴基斯坦", en: "Pakistan" },
  { value: "PW", zh: "帕劳", en: "Palau" },
  { value: "PA", zh: "巴拿马", en: "Panama" },
  { value: "PG", zh: "巴布亚新几内亚", en: "Papua New Guinea" },
  { value: "PY", zh: "巴拉圭", en: "Paraguay" },
  { value: "PE", zh: "秘鲁", en: "Peru" },
  { value: "PH", zh: "菲律宾", en: "Philippines" },
  { value: "PL", zh: "波兰", en: "Poland" },
  { value: "PT", zh: "葡萄牙", en: "Portugal" },
  { value: "QA", zh: "卡塔尔", en: "Qatar" },
  { value: "RO", zh: "罗马尼亚", en: "Romania" },
  { value: "RU", zh: "俄罗斯", en: "Russia" },
  { value: "RW", zh: "卢旺达", en: "Rwanda" },
  { value: "KN", zh: "圣基茨和尼维斯", en: "Saint Kitts and Nevis" },
  { value: "LC", zh: "圣卢西亚", en: "Saint Lucia" },
  { value: "VC", zh: "圣文森特和格林纳丁斯", en: "Saint Vincent and the Grenadines" },
  { value: "WS", zh: "萨摩亚", en: "Samoa" },
  { value: "SM", zh: "圣马力诺", en: "San Marino" },
  { value: "ST", zh: "圣多美和普林西比", en: "Sao Tome and Principe" },
  { value: "SA", zh: "沙特阿拉伯", en: "Saudi Arabia" },
  { value: "SN", zh: "塞内加尔", en: "Senegal" },
  { value: "RS", zh: "塞尔维亚", en: "Serbia" },
  { value: "SC", zh: "塞舌尔", en: "Seychelles" },
  { value: "SL", zh: "塞拉利昂", en: "Sierra Leone" },
  { value: "SG", zh: "新加坡", en: "Singapore" },
  { value: "SK", zh: "斯洛伐克", en: "Slovakia" },
  { value: "SI", zh: "斯洛文尼亚", en: "Slovenia" },
  { value: "SB", zh: "所罗门群岛", en: "Solomon Islands" },
  { value: "SO", zh: "索马里", en: "Somalia" },
  { value: "ZA", zh: "南非", en: "South Africa" },
  { value: "SS", zh: "南苏丹", en: "South Sudan" },
  { value: "ES", zh: "西班牙", en: "Spain" },
  { value: "LK", zh: "斯里兰卡", en: "Sri Lanka" },
  { value: "SD", zh: "苏丹", en: "Sudan" },
  { value: "SR", zh: "苏里南", en: "Suriname" },
  { value: "SE", zh: "瑞典", en: "Sweden" },
  { value: "CH", zh: "瑞士", en: "Switzerland" },
  { value: "SY", zh: "叙利亚", en: "Syria" },
  { value: "TJ", zh: "塔吉克斯坦", en: "Tajikistan" },
  { value: "TZ", zh: "坦桑尼亚", en: "Tanzania" },
  { value: "TH", zh: "泰国", en: "Thailand" },
  { value: "TL", zh: "东帝汶", en: "Timor-Leste" },
  { value: "TG", zh: "多哥", en: "Togo" },
  { value: "TO", zh: "汤加", en: "Tonga" },
  { value: "TT", zh: "特立尼达和多巴哥", en: "Trinidad and Tobago" },
  { value: "TN", zh: "突尼斯", en: "Tunisia" },
  { value: "TR", zh: "土耳其", en: "Turkey" },
  { value: "TM", zh: "土库曼斯坦", en: "Turkmenistan" },
  { value: "TV", zh: "图瓦卢", en: "Tuvalu" },
  { value: "UG", zh: "乌干达", en: "Uganda" },
  { value: "UA", zh: "乌克兰", en: "Ukraine" },
  { value: "AE", zh: "阿联酋", en: "United Arab Emirates" },
  { value: "GB", zh: "英国", en: "United Kingdom" },
  { value: "US", zh: "美国", en: "United States" },
  { value: "UY", zh: "乌拉圭", en: "Uruguay" },
  { value: "UZ", zh: "乌兹别克斯坦", en: "Uzbekistan" },
  { value: "VU", zh: "瓦努阿图", en: "Vanuatu" },
  { value: "VE", zh: "委内瑞拉", en: "Venezuela" },
  { value: "VN", zh: "越南", en: "Vietnam" },
  { value: "YE", zh: "也门", en: "Yemen" },
  { value: "ZM", zh: "赞比亚", en: "Zambia" },
  { value: "ZW", zh: "津巴布韦", en: "Zimbabwe" },
  { value: "PS", zh: "巴勒斯坦", en: "Palestine" },
  { value: "VA", zh: "梵蒂冈", en: "Vatican City" }
]

export default {
  name: "Link",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 链接管理表格数据
      linkList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        linkUrl: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        linkUrl: [
          { required: true, message: "分流链接不能为空", trigger: "blur" }
        ],
      }
    };
  },
  computed: {
    countryOptions() {
      const list = (COUNTRY_OPTIONS || []).map(item => ({
        value: item.value,
        label: `${item.zh} (${item.en})`
      }));
      list.sort((a, b) => String(a.label).localeCompare(String(b.label), "zh-Hans"));
      return list;
    }
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询链接管理列表 */
    getList() {
      this.loading = true;
      listLink(this.queryParams).then(response => {
        this.linkList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        linkId: null,
        targetCountry: "",
        linkUrl: null,
        linkDescription: null,
        replyMsg: null,
        ipProtection: "1",
        isScramble: "1",
        status: "0",
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.linkId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加链接管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const linkId = row.linkId || this.ids
      getLink(linkId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改链接管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.linkId != null) {
            updateLink(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addLink(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const linkIds = row.linkId || this.ids;
      this.$modal.confirm('是否确认删除链接管理编号为"' + linkIds + '"的数据项？').then(function() {
        return delLink(linkIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/link/export', {
        ...this.queryParams
      }, `link_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
