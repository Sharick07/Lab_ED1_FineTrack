package multas.ui;

import java.awt.*;
import javax.swing.*;
import multas.ui.panels.*;
import multas.utils.UIUtils;

//  Esta clase es la ventana principal de la aplicación (marco base).
//  Contiene la estructura de menús y paneles,
//  y gestiona la navegación entre ellos.
// 

public class MainFrame extends JFrame {
    private JPanel sideMenu;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private boolean menuVisible = true;
    private int menuWidth = 260; // ancho inicial del menú


    public MainFrame() {
        setTitle("FINETRACK");
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Logo (2).png")).getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        getContentPane().setBackground(UIUtils.APP_BG);
        setLayout(new BorderLayout());

        // Sidebar
        sideMenu = createSideMenu();
        add(sideMenu, BorderLayout.WEST);

        // Top bar con botón hamburguesa
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(0, 70));
        topBar.setBackground(UIUtils.PRIMARY);

        JButton btnMenu = new JButton("☰"); // usa texto hamburguesa
        btnMenu.setFont(new Font("SansSerif", Font.BOLD, 28));
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setFocusPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setPreferredSize(new Dimension(60, 60));


        btnMenu.addActionListener(e -> toggleMenu());

        topBar.add(btnMenu, BorderLayout.WEST);

        JLabel title = new JLabel("FINETRACK: Sistema de Gestión de Multas");
        title.setFont(UIUtils.TITLE_FONT);
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);

        // Content (CardLayout)
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(UIUtils.APP_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Add panels (cards)
        contentPanel.add(new HomePanel(), "home");
        contentPanel.add(new ConsultPanel(), "consult");
        contentPanel.add(new AddFinePanel(), "add");
        contentPanel.add(new RegisterPaymentPanel(), "pay");
        contentPanel.add(new ExpiredPanel(), "expired");
        contentPanel.add(new DeletePanel(), "delete");
        contentPanel.add(new DeleteReportPanel(), "reportes");


        add(contentPanel, BorderLayout.CENTER);

        // show default
        showCard("home");
    }

    private JPanel createSideMenu() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(menuWidth, getHeight()));
        p.setLayout(new BorderLayout());
        p.setBackground(UIUtils.LIGHT_BLUE);

        // Logo area
        JPanel top = new JPanel();
        top.setBackground(UIUtils.LIGHT_BLUE);
        top.setPreferredSize(new Dimension(menuWidth, 120));
        top.setLayout(new GridBagLayout());
        JLabel logo = new JLabel(UIUtils.getScaledIcon("/imagenes/Logo (2).png", 96, 96));
        top.add(logo);
        p.add(top, BorderLayout.NORTH);

        // Buttons area
        JPanel buttons = new JPanel();
        buttons.setBackground(UIUtils.LIGHT_BLUE);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        buttons.add(createNavButton("Home", "/imagenes/Home.png", "home"));
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(createNavButton("Consultar multas", "/imagenes/buscar.png", "consult"));
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(createNavButton("Agregar multa", "/imagenes/agregar.png", "add"));
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(createNavButton("Pagar multa", "/imagenes/pagar.png", "pay"));
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(createNavButton("Ver multas vencidas", "/imagenes/vencidas.png", "expired"));
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(createNavButton("Eliminar multas pagadas", "/imagenes/eliminar.png", "delete"));
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(createNavButton("Reporte de multas eliminadas", "/imagenes/reporte_1.png", "reportes"));
        p.add(buttons, BorderLayout.CENTER);

        // footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(UIUtils.LIGHT_BLUE);
        JLabel ver = new JLabel("<html><small style='color:#ffffff'>v1.0 — NextGen Systems</small></html>");
        footer.add(ver);
        footer.setPreferredSize(new Dimension(menuWidth, 40));
        p.add(footer, BorderLayout.SOUTH);

        return p;
    }

    private JButton createNavButton(String text, String iconPath, String cardName) {
        JButton btn = new JButton(text, UIUtils.getScaledIcon(iconPath, 20, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setForeground(UIUtils.WHITE);
        btn.setBackground(UIUtils.LIGHT_BLUE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setMaximumSize(new Dimension(240, 48));
        btn.setPreferredSize(new Dimension(240, 48));
        btn.setFont(UIUtils.MENU_FONT);

        btn.addActionListener(e -> showCard(cardName));
        // hover effect
        btn.addMouseListener(UIUtils.createHoverEffect(btn, UIUtils.LIGHT_BLUE, UIUtils.SIDEBAR_HOVER_BG));
        return btn;
    }
    
    private void toggleMenu() {
    int targetWidth = menuVisible ? 0 : 260; // si está visible → colapsar, si no → expandir
    int step = menuVisible ? -10 : 10; // velocidad de animación

    Timer timer = new Timer(10, null);
    timer.addActionListener(ev -> {
        menuWidth += step;
        if ((step < 0 && menuWidth <= targetWidth) || (step > 0 && menuWidth >= targetWidth)) {
            menuWidth = targetWidth;
            timer.stop();
            menuVisible = !menuVisible;
        }
        sideMenu.setPreferredSize(new Dimension(menuWidth, getHeight()));
        sideMenu.revalidate();       // 🔹 recalcula tamaño del panel
        sideMenu.repaint();          // 🔹 repinta el menú
        getContentPane().revalidate();
        getContentPane().repaint();
    });
    timer.start();
}


    public void showCard(String name) {
        cardLayout.show(contentPanel, name);
    }
}

